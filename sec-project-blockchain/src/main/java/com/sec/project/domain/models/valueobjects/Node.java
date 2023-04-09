package com.sec.project.domain.models.valueobjects;

import com.sec.project.domain.models.enums.Mode;
import com.sec.project.domain.models.enums.Role;
import com.sec.project.domain.models.records.Connection;
import com.sec.project.domain.models.records.Message;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

import static com.sec.project.utils.Constants.BYZANTINE_RANDOM_STRING;

/**
 * Node data class that contains the variables needed to maintain a Node in the blockchain network.
 */
public class Node {

    private Role role;
    private Mode mode;
    private final Connection connection;
    private final UUID uuid = UUID.randomUUID();

    /**
     * Logic to construct a blockchain node.
     *
     * @param port where the node is being bound to.
     * @param role of the current node.
     * @param mode on how the current node should behaviour itself.
     * @throws SocketException      in case any socket issue is encountered.
     * @throws UnknownHostException in case the process cant resolve the host.
     */
    public Node(int port, Role role, Mode mode) throws SocketException, UnknownHostException {
        this.role = role;
        this.mode = mode;
        this.connection = new Connection(port);
    }

    /**
     * Returns the mode for the active node in the blockchain process.
     *
     * @return current node mode.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Changes the mode of the current node to a specific one.
     *
     * @param mode to set on the current node.
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Returns the role for the active node in the blockchain process.
     *
     * @return current node role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Changes the role of the current node to a specific one.
     *
     * @param role to set on the current node.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the current node connection.
     *
     * @return connection object that has the structures needed to send remote messages.
     * @see Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns the unique identifier of the current blockchain node.
     *
     * @return of the node.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Modifies a message, choosing a random option on how to modify it.
     *
     * @param message to be modified by the byzantine process.
     * @return modified message.
     */
    public Message craftByzantineMessage(Message message) {
        enum Action {CHANGE_ROUND, CHANGE_VALUE, CHANGE_ID}
        Action random = Action.values()[new Random().nextInt(Action.values().length)];
        return switch (random) {
            case CHANGE_ID -> new Message(message.type(), new Random().nextLong(), message.round(), message.value(), 0,0);
            case CHANGE_ROUND -> new Message(message.type(), message.id(), new Random().nextLong(), message.value(), 0,0);
            case CHANGE_VALUE -> new Message(message.type(), message.id(), message.round(), BYZANTINE_RANDOM_STRING, 0,0);
        };
    }

}
