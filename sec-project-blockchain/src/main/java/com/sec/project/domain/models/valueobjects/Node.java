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

public class Node {

    private Role role;
    private Mode mode;
    private final Connection connection;
    private final UUID uuid = UUID.randomUUID();

    public Node(int port, Role role, Mode mode) throws SocketException, UnknownHostException {
        this.role = role;
        this.mode = mode;
        this.connection = new Connection(port);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Connection getConnection() {
        return connection;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Message craftByzantineMessage(Message message) {
        enum Action {CHANGE_ROUND, CHANGE_VALUE, CHANGE_ID}
        Action random = Action.values()[new Random().nextInt(Action.values().length)];
        return switch (random) {
            case CHANGE_ID -> new Message(message.type(), new Random().nextLong(), message.round(), message.value());
            case CHANGE_ROUND -> new Message(message.type(), message.id(), new Random().nextLong(), message.value());
            case CHANGE_VALUE -> new Message(message.type(), message.id(), message.round(), BYZANTINE_RANDOM_STRING);
        };
    }

}
