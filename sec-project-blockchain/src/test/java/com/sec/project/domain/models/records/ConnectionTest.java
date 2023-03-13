package com.sec.project.domain.models.records;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConnectionTest {

    private static final DatagramSocket socket;
    private static final int testingPort = 5000;
    private static final InetAddress localHostAddress;

    static {
        try {
            socket = new DatagramSocket(testingPort);
            localHostAddress = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void beforeEach() {
        socket.close();
    }

    @Test
    public void testConnection() {
        Connection connection = new Connection(localHostAddress, socket);
        assertNotNull(connection);
        assertEquals(socket, connection.datagramSocket());
        assertEquals(localHostAddress, connection.address());
    }

    @Test
    public void testConnectionPort() throws UnknownHostException, SocketException {
        Connection connection = new Connection(testingPort);
        assertNotNull(connection);
        assertEquals(testingPort, connection.datagramSocket().getLocalPort());
    }

}