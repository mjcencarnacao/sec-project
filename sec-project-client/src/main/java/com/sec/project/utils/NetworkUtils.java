package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.domain.models.Connection;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

@Component
public class NetworkUtils<T> {

    private final Gson gson;
    private final Connection connection;
    private final StaticNodeConfiguration staticNodeConfiguration;

    @Autowired
    public NetworkUtils(Gson gson, Connection connection, StaticNodeConfiguration staticNodeConfiguration) {
        this.gson = gson;
        this.connection = connection;
        this.staticNodeConfiguration = staticNodeConfiguration;
    }

    public void sendMessage(T object) {
        byte[] bytes = gson.toJson(object).getBytes();
        staticNodeConfiguration.ports.forEach(port -> deliverPacket(bytes, port));
    }

    public T receiveResponse(Class<T> objectClass) {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
        try (DatagramSocket socket = connection.datagramSocket()) {
            socket.receive(dataReceived);
            return gson.fromJson(new String(buffer), objectClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deliverPacket(byte[] bytes, int port) {
        try (DatagramSocket socket = connection.datagramSocket()) {
            socket.send(new DatagramPacket(bytes, bytes.length, socket.getInetAddress(), port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
