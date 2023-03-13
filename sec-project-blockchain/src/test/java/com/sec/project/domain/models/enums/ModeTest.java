package com.sec.project.domain.models.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sec.project.domain.models.enums.Mode.BYZANTINE;
import static com.sec.project.domain.models.enums.Mode.REGULAR;
import static org.junit.jupiter.api.Assertions.*;

class ModeTest {

    private static final int ModeLength = 2;

    @Test
    public void testMessageTypeValues() {
        Arrays.stream(Mode.values()).forEach(type -> assertEquals(type, Mode.valueOf(type.name())));
    }

    @Test
    public void testByzantineProperty() {
        assertFalse(REGULAR.isByzantine());
        assertTrue(BYZANTINE.isByzantine());
    }

    @Test
    public void testMessageTypeOrdinals() {
        AtomicInteger ordinal = new AtomicInteger();
        Arrays.stream(Mode.values()).forEach(type -> {
            assertEquals(ordinal.get(), type.ordinal());
            ordinal.getAndIncrement();
        });
    }

    @Test
    public void testMessageTypeLength() {
        assertEquals(ModeLength, Mode.values().length);
    }

}