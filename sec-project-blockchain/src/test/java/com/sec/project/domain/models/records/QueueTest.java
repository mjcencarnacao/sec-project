package com.sec.project.domain.models.records;

import com.sec.project.domain.models.enums.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QueueTest {

    private final long testId = 0;
    private final long testRound = 0;
    private final Queue queue = new Queue();
    private final String testValue = "TEST_VALUE";
    private final MessageType testMessageType = MessageType.PREPARE;
    private final Message testMessage = new Message(testMessageType, testId, testRound, testValue);

    @Test
    public void testQueueCreation() {
        assertNotNull(queue);
    }

    @Test
    public void testQueueMessageStorage() {
        queue.queue().add(testMessage);
        assertEquals(1, queue.queue().size());
        assertEquals(queue.queue().get(0), testMessage);
    }

}