package com.sec.project.domain.models.valueobjects;

import com.sec.project.domain.models.enums.MessageType;
import com.sec.project.domain.models.enums.Mode;
import com.sec.project.domain.models.enums.Role;
import com.sec.project.domain.models.records.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NodeTest {

    private Node testNode;
    private static final int testPort = 5000;
    private final Role testRole = Role.MEMBER;
    private final Mode testMode = Mode.BYZANTINE;

    @AfterEach
    public void afterEach() {
        testNode.getConnection().datagramSocket().close();
    }

    @Test
    public void testCraftByzantineMessage() throws SocketException, UnknownHostException {
        Message originalMessage = new Message(MessageType.PREPARE, 0, 0, "TEST_VALUE");
        testNode = new Node(testPort, testRole, testMode);
        Message modifiedMessage = testNode.craftByzantineMessage(originalMessage);
        assertNotEquals(originalMessage, modifiedMessage);
    }

}