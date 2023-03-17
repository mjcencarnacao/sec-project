package com.sec.project.domain.models.enums;

/**
 * Different modes that a given node can assume.
 */
public enum Mode {

    BYZANTINE, REGULAR;

    public boolean isByzantine() {
        return this == BYZANTINE;
    }

}
