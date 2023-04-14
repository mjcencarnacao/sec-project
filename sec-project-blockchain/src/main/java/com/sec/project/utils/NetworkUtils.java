package com.sec.project.utils;

import com.google.gson.Gson;
import com.sec.project.configuration.SecurityConfiguration;
import com.sec.project.configuration.StaticNodeConfiguration;
import com.sec.project.models.enums.SendingMethod;
import com.sec.project.models.records.MessageTransferObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.configuration.StaticNodeConfiguration.ports;
import static com.sec.project.infrastructure.repositories.ConsensusServiceImplementation.blockchainTransactions;
import static com.sec.project.interfaces.CommandLineInterface.clientListener;
import static com.sec.project.interfaces.CommandLineInterface.self;
import static com.sec.project.utils.Constants.DEFAULT_TIMEOUT;
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
    private final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);
    private final HashMap<Integer, DatagramPacket> packetRecordQueue = new HashMap<>();

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
            case BROADCAST -> ports.forEach(port -> createPacketForDelivery(message, port));
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
            packetRecordQueue.remove(dataReceived.getPort());
            MessageTransferObject message = gson.fromJson(new String(buffer).trim(), MessageTransferObject.class);
            if (getPublicKeysFromFile(false).get(dataReceived.getPort()) != null && securityConfiguration.verifySignature(getPublicKeysFromFile(false).get(dataReceived.getPort()), message.data(), message.signature()))
                return new ImmutablePair<>(dataReceived.getPort(), message);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that handles a quorum of responses for a specified class.
     *
     * @param objectClass required to allow the recovery of the original object type.
     * @return the Object, of which the class is passed as an argument.
     */
    public ImmutablePair<T, List<byte[]>> receiveQuorumResponse(Class<T> objectClass) {
        ImmutablePair<List<T>, List<byte[]>> responseSignature = new ImmutablePair<>(new LinkedList<>(), new LinkedList<>());
        new Thread(this::waitForAcknowledges).start();
        AtomicReference<T> nonByzantineMessage = new AtomicReference<>();
        while (responseSignature.left.size() != ports.size()) {
            ImmutablePair<Integer, MessageTransferObject> response = receiveResponse();
            responseSignature.right.add(response.right.signature());
            responseSignature.left.add(gson.fromJson(new String(response.right.data()).trim(), objectClass));
        }
        responseSignature.left.forEach(message -> {
            if (securityConfiguration.generateMessageDigest(gson.toJson(message).getBytes()).equals(hasQuorumOfValidMessages(responseSignature.left)))
                nonByzantineMessage.set(message);
        });
        return new ImmutablePair<>(nonByzantineMessage.get(), responseSignature.right);
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
            if (ports.contains(port)) packetRecordQueue.put(port, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForAcknowledges() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (packetRecordQueue.isEmpty())
                    timer.cancel();
                resendPacketsFromRecordQueue();
            }
        };
        timer.scheduleAtFixedRate(task, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
    }

    private void resendPacketsFromRecordQueue() {
        DatagramSocket socket = self.getConnection().datagramSocket();
        packetRecordQueue.forEach((port, packet) -> {
            try {
                logger.info("Retransmitting packet to port: " + port);
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void enqueueClientRequests() {
        while (true) {
            try {
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
                DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
                clientListener.receive(dataReceived);
                MessageTransferObject message = gson.fromJson(new String(buffer).trim(), MessageTransferObject.class);
                blockchainTransactions.clientRequests().add(new ImmutablePair<>(dataReceived.getPort(), message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
