package com.sec.project.domain.models.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sec.project.domain.models.enums.Role.LEADER;
import static com.sec.project.domain.models.enums.Role.MEMBER;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private static final int RoleLength = 2;

    @Test
    public void testRoleValues() {
        Arrays.stream(Role.values()).forEach(type -> assertEquals(type, Role.valueOf(type.name())));
    }

    @Test
    public void testMessageTypeOrdinals() {
        AtomicInteger ordinal = new AtomicInteger();
        Arrays.stream(Role.values()).forEach(type -> {
            assertEquals(ordinal.get(), type.ordinal());
            ordinal.getAndIncrement();
        });
    }

    @Test
    public void testMessageTypeLength() {
        assertEquals(RoleLength, Role.values().length);
    }

    @Test
    void testIsLeaderProperty() {
        assertTrue(LEADER.isLeader());
        assertFalse(MEMBER.isLeader());
    }

    @Test
    void testIsMemberProperty() {
        assertFalse(LEADER.isMember());
        assertTrue(MEMBER.isMember());
    }
}