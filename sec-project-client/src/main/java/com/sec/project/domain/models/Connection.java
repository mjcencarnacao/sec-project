package com.sec.project.domain.models;

import org.springframework.stereotype.Component;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Component
public record Connection(InetAddress address, DatagramSocket datagramSocket) {
    public Connection() throws UnknownHostException, SocketException {
        this(InetAddress.getLocalHost(), new DatagramSocket());
    }
}
