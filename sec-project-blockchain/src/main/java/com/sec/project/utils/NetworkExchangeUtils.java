package com.sec.project.utils;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Node;
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

import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

@Component
@SuppressWarnings("unchecked")
public class NetworkExchangeUtils<T> {

    private final Node self;
    private final ConversionUtils<T> conversionUtils;
    private final StaticNodeConfiguration staticNodeConfiguration;
    private final Logger logger = LoggerFactory.getLogger(NetworkExchangeUtils.class);

    @Autowired
    public NetworkExchangeUtils(Node self, ConversionUtils<T> conversionUtils, StaticNodeConfiguration staticNodeConfiguration) {
        this.self = self;
        this.conversionUtils = conversionUtils;
        this.staticNodeConfiguration = staticNodeConfiguration;
    }

    public void sendMessage(T object, SendingMethod sendingMethod, Optional<Integer> receiver) {
        byte[] bytes = conversionUtils.convertObjectToBytes(object);
        switch (sendingMethod) {
            case UNICAST -> receiver.ifPresent(integer -> createPacketForDelivery(bytes, integer));
            case BROADCAST -> staticNodeConfiguration.ports.forEach(port -> createPacketForDelivery(bytes, port));
            default -> throw new IllegalArgumentException(String.format("Unknown sending method with value %s", sendingMethod.name()));
        }
    }

    public CompletableFuture<T> receiveResponse() {
        T message = null;
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
        try {
            self.connection().datagramSocket().receive(dataReceived);
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(buffer));
            message = (T) stream.readObject();
            stream.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(message);
    }

    private void createPacketForDelivery(byte[] bytes, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, self.connection().address(), port);
            self.connection().datagramSocket().send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
