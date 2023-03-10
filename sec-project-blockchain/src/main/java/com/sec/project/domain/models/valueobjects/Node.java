package com.sec.project.domain.models.valueobjects;

import com.sec.project.domain.models.enums.Mode;
import com.sec.project.domain.models.enums.Role;
import com.sec.project.domain.models.records.Connection;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

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

}
