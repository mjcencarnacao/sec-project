package com.sec.project.domain.models.enums;

public enum Role {

    LEADER, MEMBER;

    public boolean isLeader() {
        return this == LEADER;
    }

    public boolean isMember() {
        return this == MEMBER;
    }
}
