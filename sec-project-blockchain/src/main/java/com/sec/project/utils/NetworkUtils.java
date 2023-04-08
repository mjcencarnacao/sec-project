package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sec.project.infrastructure.configuration.StaticNodeConfiguration.publicKeyHashMap;
import static com.sec.project.interfaces.CommandLineInterface.self;
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
    private final SecurityConfiguration securityConfiguration;

    @Autowired
    public NetworkUtils(Gson gson, SecurityConfiguration securityConfiguration) {
        this.gson = gson;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Method that allows for a generic object to be converted to bytes and sent given a specific sending method.
     *
     * @param object        generic object to be sent.
     * @param sendingMethod sending method that can either be a UNICAST or a BROADCAST.
     * @param receiver      optional parameter specifying the receiver if the sending method is a UNICAST.
     * @throws IllegalArgumentException for unknown sending methods.
     */
    public void sendMessage(T object, SendingMethod sendingMethod, Optional<Integer> receiver) {
        byte[] objectBytes = gson.toJson(object).getBytes();
        byte[] message = gson.toJson(new MessageTransferObject(objectBytes, securityConfiguration.signMessage(objectBytes))).getBytes();
        switch (sendingMethod) {
            case UNICAST -> receiver.ifPresent(integer -> createPacketForDelivery(message, integer));
            case BROADCAST -> StaticNodeConfiguration.ports.forEach(port -> createPacketForDelivery(message, port));
            default -> throw new IllegalArgumentException(String.format("Unknown value %s", sendingMethod.name()));
        }
    }

    /**
     * Method that handles logic for receiving responses from remote nodes.
     *
     * @return the Object, of which the class is passed as an argument.
     * @throws RuntimeException in case of any Input/Output errors.
     */
    public ImmutablePair<Integer, MessageTransferObject> receiveResponse() {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        try {
            DatagramSocket socket = self.getConnection().datagramSocket();
            DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
            socket.receive(dataReceived);
            MessageTransferObject message = gson.fromJson(new String(buffer).trim(), MessageTransferObject.class);
            if (securityConfiguration.verifySignature(publicKeyHashMap.get(dataReceived.getPort()), message.data(), message.signature()))
                return new ImmutablePair<>(dataReceived.getPort(), message);
            return new ImmutablePair<>(dataReceived.getPort(), message);
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
        while (responses.size() != StaticNodeConfiguration.ports.size())
            responses.add(gson.fromJson(new String(receiveResponse().right.data()).trim(), objectClass));
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
     * Method that handles logic to send a datagram packet via UDP.
     *
     * @param bytes buffer containing the bytes of the message to be sent.
     * @param port  destination port of the remote node.
     */
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
