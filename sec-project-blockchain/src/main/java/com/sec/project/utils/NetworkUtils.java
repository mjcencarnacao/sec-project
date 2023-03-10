package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sec.project.interfaces.CommandLineInterface.self;
import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

@Component
public class NetworkUtils<T> {

    private final Gson gson;
    private final StaticNodeConfiguration staticNodeConfiguration;
    private final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

    @Autowired
    public NetworkUtils(Gson gson, StaticNodeConfiguration staticNodeConfiguration) {
        this.gson = gson;
        this.staticNodeConfiguration = staticNodeConfiguration;
    }

    public void sendMessage(T object, SendingMethod sendingMethod, Optional<Integer> receiver) {
        byte[] bytes = gson.toJson(object).getBytes();
        switch (sendingMethod) {
            case UNICAST -> receiver.ifPresent(integer -> createPacketForDelivery(bytes, integer));
            case BROADCAST -> staticNodeConfiguration.ports.forEach(port -> createPacketForDelivery(bytes, port));
            default -> throw new IllegalArgumentException(String.format("Unknown value %s", sendingMethod.name()));
        }
    }

    public T receiveResponse(Class<T> objectClass) {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        try {
            DatagramSocket socket = self.getConnection().datagramSocket();
            DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
            socket.receive(dataReceived);
            System.out.println(gson.fromJson(new String(buffer).trim(), objectClass));
            return gson.fromJson(new String(buffer).trim(), objectClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> receiveQuorumResponse(Class<T> objectClass) {
        List<T> responses = new ArrayList<>();
        while (responses.size() != staticNodeConfiguration.getQuorum())
            responses.add(receiveResponse(objectClass));
        return responses;
    }

    private void createPacketForDelivery(byte[] bytes, int port) {
        try {
            DatagramSocket socket = self.getConnection().datagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, self.getConnection().address(), port);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
