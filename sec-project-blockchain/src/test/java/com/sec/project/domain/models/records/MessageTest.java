package com.sec.project.domain.models.records;

import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {

    private final long testId = 0;
    private final long testRound = 0;
    private final String testValue = "TEST_VALUE";
    private final MessageType testMessageType = MessageType.PREPARE;
    private final Message testMessage = new Message(testMessageType, testId, testRound, testValue);

    @Test
    public void testMessageCreation() {
        assertNotNull(testMessage);
    }

    @Test
    public void testMessageType() {
        assertEquals(testMessageType, testMessage.type());
    }

    @Test
    public void testMessageId() {
        assertEquals(testId, testMessage.id());
    }

    @Test
    public void testMessageRound() {
        assertEquals(testRound, testMessage.round());
    }

    @Test
    public void testMessageValue() {
        assertEquals(testValue, testMessage.value());
    }

}