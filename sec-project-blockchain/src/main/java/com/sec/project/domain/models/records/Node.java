package com.sec.project.domain.models.records;

import com.sec.project.domain.models.enums.Role;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

@Component
public record Node(UUID uuid, Role role, Connection connection) {
    public Node() throws SocketException, UnknownHostException {
        this(UUID.randomUUID(), Role.MEMBER, new Connection());
    }
}
