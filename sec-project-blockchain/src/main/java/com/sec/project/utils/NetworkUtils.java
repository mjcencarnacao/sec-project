package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import org.apache.commons.lang3.StringUtils;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public void sendMessage(T object, SendingMethod sendingMethod, Optional<Integer> receiver, boolean encryptionDisabled) {
        byte[] objectBytes = gson.toJson(object).getBytes();
        MessageTransferObject message = new MessageTransferObject(objectBytes);
        byte[] encryptedBytes = encryptionDisabled ? gson.toJson(message).getBytes() : securityConfiguration.symmetricEncoding(gson.toJson(message).getBytes());
        switch (sendingMethod) {
            case UNICAST -> receiver.ifPresent(integer -> createPacketForDelivery(encryptedBytes, integer));
            case BROADCAST -> StaticNodeConfiguration.ports.forEach(port -> createPacketForDelivery(encryptedBytes, port));
            default -> throw new IllegalArgumentException(String.format("Unknown value %s", sendingMethod.name()));
        }
    }

    /**
     * Method that handles logic for receiving responses from remote nodes.
     *
     * @return the Object, of which the class is passed as an argument.
     * @throws RuntimeException in case of any Input/Output errors.
     */
    public ImmutablePair<Integer, MessageTransferObject> receiveResponse(boolean encryptionDisabled) {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        try {
            DatagramSocket socket = self.getConnection().datagramSocket();
            DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
            socket.receive(dataReceived);
            byte[] decryptedBytes = encryptionDisabled ? buffer : securityConfiguration.symmetricDecoding(new String(addPadding(new String(buffer).trim().getBytes())).getBytes());
            return new ImmutablePair<>(dataReceived.getPort(), gson.fromJson(new String(decryptedBytes).trim(), MessageTransferObject.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add padding to encrypted messages in order for the blocks be dividable by 16.
     *
     * @param input message that requires padding.
     * @return padded message.
     */
    private byte[] addPadding(byte[] input) {
        int paddingSize = 0;
        while (input.length + paddingSize % 16 != 0) paddingSize++;
        return StringUtils.rightPad(new String(input), paddingSize, " ").getBytes();
    }

    /**
     * Method that handles a quorum of responses for a specified class.
     *
     * @param objectClass required to allow the recovery of the original object type.
     * @return the Object, of which the class is passed as an argument.
     */
    public T receiveQuorumResponse(Class<T> objectClass) {
        List<T> responses = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        while (responses.size() != StaticNodeConfiguration.ports.size())
            responses.add(gson.fromJson(new String(receiveResponse(true).right.data()).trim(), objectClass));
        responses.forEach(response -> hashes.add(securityConfiguration.generateMessageDigest(gson.toJson(response).getBytes())));
        long x = hashes.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();
        System.out.println(x);
        if (x >= StaticNodeConfiguration.getQuorum()) return responses.get(0);
        return null;
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
