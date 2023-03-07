package com.sec.project.utils;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.sec.project.interfaces.CommandLineInterface.self;
import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

@Component
@SuppressWarnings("unchecked")
public class NetworkUtils<T> {

    private final ConversionUtils<T> conversionUtils;
    private final StaticNodeConfiguration staticNodeConfiguration;
    private final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

    @Autowired
    public NetworkUtils(ConversionUtils<T> conversionUtils, StaticNodeConfiguration staticNodeConfiguration) {
        this.conversionUtils = conversionUtils;
        this.staticNodeConfiguration = staticNodeConfiguration;
    }

    public void sendMessage(T object, SendingMethod sendingMethod, Optional<Integer> receiver) {
        byte[] bytes = conversionUtils.convertObjectToBytes(object);
        switch (sendingMethod) {
            case UNICAST -> receiver.ifPresent(integer -> createPacketForDelivery(bytes, integer));
            case BROADCAST -> staticNodeConfiguration.ports.forEach(port -> createPacketForDelivery(bytes, port));
            default -> throw new IllegalArgumentException(String.format("Unknown value %s", sendingMethod.name()));
        }
    }

    public T receiveResponse() {
        T message;
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
        try {
            self.getConnection().datagramSocket().receive(dataReceived);
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(buffer));
            message = (T) stream.readObject();
            stream.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    private void createPacketForDelivery(byte[] bytes, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, self.getConnection().address(), port);
            self.getConnection().datagramSocket().send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
