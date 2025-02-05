package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.configuration.SecurityConfiguration;
import com.sec.project.configuration.StaticNodeConfiguration;
import com.sec.project.models.enums.ReadType;
import com.sec.project.models.records.Connection;
import com.sec.project.models.records.MessageTransferObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.configuration.StaticNodeConfiguration.ports;
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
    public static byte[] lastReceived = null;
    public static Connection connection = null;
    private final SecurityConfiguration securityConfiguration;

    @Autowired
    public NetworkUtils(Gson gson, SecurityConfiguration securityConfiguration) throws SocketException, UnknownHostException {
        this.gson = gson;
        connection = new Connection();
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Method that allows for a generic object to be converted to bytes and broadcasts to every node on the chain.
     *
     * @param object generic object to be sent to every node on the blockchain. This is parsed as JSON and recovered to the
     *               original object on the remote side.
     */
    public void sendMessage(T object, ReadType readType) {
        byte[] bytes = gson.toJson(object).getBytes();
        MessageTransferObject message = new MessageTransferObject(bytes, securityConfiguration.signMessage(bytes));
        if (readType == ReadType.STRONGLY_CONSISTENT_READ)
            StaticNodeConfiguration.ports.forEach(port -> deliverPacket(gson.toJson(message).getBytes(), port + 1000));
        else
            deliverPacket(gson.toJson(message).getBytes(), ports.get(new Random().nextInt(4)) + 1000);
    }

    /**
     * Method that handles logic for receiving responses from remote nodes.
     *
     * @param objectClass required to allow the recovery of the original object type.
     * @return the Object, of which the class is passed as an argument.
     */
    public T receiveResponse(Class<T> objectClass) {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        try {
            DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
            connection.datagramSocket().receive(dataReceived);
            MessageTransferObject message = gson.fromJson(new String(buffer).trim(), MessageTransferObject.class);
            if (getPublicKeysFromFile(false).get(dataReceived.getPort()) != null && securityConfiguration.verifySignature(getPublicKeysFromFile(false).get(dataReceived.getPort()), message.data(), message.signature()))
                return gson.fromJson(new String(message.data()).trim(), objectClass);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that handles a quorum of responses for a specified class.
     *
     * @param objectClass required to allow the recovery of the original object type.
     * @return the Object, of which the class is passed as an argument.
     */
    public T receiveQuorumResponse(Class<T> objectClass) {
        List<T> responses = new ArrayList<>();
        AtomicReference<T> nonByzantineMessage = new AtomicReference<>();
        while (responses.size() != ports.size())
            responses.add(receiveResponse(objectClass));
        responses.forEach(message -> {
            if (securityConfiguration.generateMessageDigest(gson.toJson(message).getBytes()).equals(hasQuorumOfValidMessages(responses)))
                nonByzantineMessage.set(message);
        });
        return nonByzantineMessage.get();
    }

    /**
     * Returns the hash of the valid messages in the Quorum.
     *
     * @param messages list
     * @return valid hash
     */
    private String hasQuorumOfValidMessages(List<T> messages) {
        List<String> hashes = new ArrayList<>();
        AtomicReference<ImmutablePair<String, Long>> predominantHash = new AtomicReference<>();
        messages.forEach(response -> hashes.add(securityConfiguration.generateMessageDigest(gson.toJson(response).getBytes())));
        hashes.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).ifPresent(max -> predominantHash.set(new ImmutablePair<>(max.getKey(), max.getValue())));
        return predominantHash.get().right >= StaticNodeConfiguration.getQuorum() ? predominantHash.get().left : null;
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
