package com.sec.project.domain.models.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTypeTest {

    private static final int MessageTypeLength = 4;

    @Test
    public void testMessageTypeValues() {
        Arrays.stream(MessageType.values()).forEach(type -> assertEquals(type, MessageType.valueOf(type.name())));
    }

    @Test
    public void testMessageTypeOrdinals() {
        AtomicInteger ordinal = new AtomicInteger();
        Arrays.stream(MessageType.values()).forEach(type -> {
            assertEquals(ordinal.get(), type.ordinal());
            ordinal.getAndIncrement();
        });
    }

    @Test
    public void testMessageTypeLength() {
        assertEquals(MessageTypeLength, MessageType.values().length);
    }

}