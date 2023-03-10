package com.sec.project.domain.models.enums;

public enum Mode {

    BYZANTINE, REGULAR;

    public boolean isByzantine() {
        return this == BYZANTINE;
    }

}
