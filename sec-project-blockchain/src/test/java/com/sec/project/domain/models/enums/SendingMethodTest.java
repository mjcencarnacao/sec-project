package com.sec.project.domain.models.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SendingMethodTest {

    private static final int SendingMethodLength = 2;

    @Test
    public void testSendingMethodValues() {
        Arrays.stream(SendingMethod.values()).forEach(type -> assertEquals(type, SendingMethod.valueOf(type.name())));
    }

    @Test
    public void testSendingMethodOrdinals() {
        AtomicInteger ordinal = new AtomicInteger();
        Arrays.stream(SendingMethod.values()).forEach(type -> {
            assertEquals(ordinal.get(), type.ordinal());
            ordinal.getAndIncrement();
        });
    }

    @Test
    public void testSendingMethodLength() {
        assertEquals(SendingMethodLength, SendingMethod.values().length);
    }

}