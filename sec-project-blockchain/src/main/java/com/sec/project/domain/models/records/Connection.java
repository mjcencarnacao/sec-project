package com.sec.project.domain.models.records;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public record Connection(InetAddress address, DatagramSocket datagramSocket) {
    public Connection() throws UnknownHostException, SocketException {
        this(InetAddress.getLocalHost(), new DatagramSocket());
    }
}
