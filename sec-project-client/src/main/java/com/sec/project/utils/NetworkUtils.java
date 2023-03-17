package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.domain.models.Connection;
import com.sec.project.domain.models.MessageTransferObject;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

/**
 * NetworkUtils class that implements logic for handling the UDP service, allowing delivery and reception of messages.
 *
 * @param <T> Generic class, allowing this service to be used by different kinds of types. Different objects can be sent
 *            using the component.
 */
@Component
public class NetworkUtils<T> {

    private final Gson gson;
    private final Connection connection;
    private final StaticNodeConfiguration staticNodeConfiguration;
    private final SecurityConfiguration<byte[]> securityConfiguration;

    @Autowired
    public NetworkUtils(Gson gson, Connection connection, StaticNodeConfiguration staticNodeConfiguration, SecurityConfiguration<byte[]> securityConfiguration) {
        this.gson = gson;
        this.connection = connection;
        this.staticNodeConfiguration = staticNodeConfiguration;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Method that allows for a generic object to be converted to bytes and broadcasts to every node on the chain.
     *
     * @param object generic object to be sent to every node on the blockchain. This is parsed as JSON and recovered to the
     *               original object on the remote side.
     */
    public void sendMessage(T object) {
        byte[] bytes = gson.toJson(object).getBytes();
        MessageTransferObject message = new MessageTransferObject(bytes, securityConfiguration.signMessage(bytes));
        System.out.println(gson.toJson(message));
        staticNodeConfiguration.ports.forEach(port -> deliverPacket(gson.toJson(message).getBytes(), port));
    }

    /**
     * Method that handles logic for receiving responses from remote nodes.
     *
     * @param objectClass required to allow the recovery of the original object type.
     * @return the Object, of which the class is passed as an argument.
     */
    public T receiveResponse(Class<T> objectClass) {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
        try (DatagramSocket socket = connection.datagramSocket()) {
            socket.receive(dataReceived);
            MessageTransferObject response = gson.fromJson(new String(buffer), MessageTransferObject.class);
            return gson.fromJson(new String(response.data()), objectClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that handles logic to send a datagram packet via UDP. Needed for broadcasting messages.
     *
     * @param bytes buffer containing the bytes of the message to be sent.
     * @param port  destination port of the remote node.
     */
    private void deliverPacket(byte[] bytes, int port) {
        try {
            DatagramSocket socket = connection.datagramSocket();
            socket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
