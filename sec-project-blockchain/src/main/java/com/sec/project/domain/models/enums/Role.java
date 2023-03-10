package com.sec.project.domain.models.enums;

/**
 * Different roles that a given node can assume.
 */
public enum Role {

    LEADER, MEMBER;

    public boolean isLeader() {
        return this == LEADER;
    }

    public boolean isMember() {
        return this == MEMBER;
    }
}
