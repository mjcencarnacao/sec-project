package com.sec.project.models.records;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Record that creates a UDP Socket using the local address and binds it to the blockchain node process.
 *
 * @param address        InetAddress IPv4 address, typically, localhost.
 * @param datagramSocket UDP Datagram Socket where data is going to be sent and received through.
 */
public record Connection(InetAddress address, DatagramSocket datagramSocket) {
    public Connection() throws UnknownHostException, SocketException {
        this(InetAddress.getLocalHost(), new DatagramSocket());
    }

    public Connection(int port) throws UnknownHostException, SocketException {
        this(InetAddress.getLocalHost(), new DatagramSocket(port));
    }
}
