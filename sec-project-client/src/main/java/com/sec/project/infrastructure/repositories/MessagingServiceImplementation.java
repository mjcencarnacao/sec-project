package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.models.Connection;
import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import com.sec.project.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.concurrent.CompletableFuture;

import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

@Service
public class MessagingServiceImplementation implements MessagingService {

    private final Connection connection;
    private final ConversionUtils<Message> conversionUtils;
    Logger logger = LoggerFactory.getLogger(MessagingServiceImplementation.class);

    @Autowired
    public MessagingServiceImplementation(ConversionUtils<Message> conversionUtils, Connection connection) {
        this.connection = connection;
        this.conversionUtils = conversionUtils;
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        byte[] bytes = conversionUtils.convertObjectToBytes(message);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, connection.address(), 1234);
        connection.datagramSocket().send(packet);
    }

    @Override
    public CompletableFuture<Message> receiveResponse() {
        Message message = null;
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
        try {
            connection.datagramSocket().receive(dataReceived);
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(buffer));
            message = (Message) stream.readObject();
            stream.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(message);
    }
}
