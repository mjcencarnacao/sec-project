package com.sec.project.domain.models.enums;

/**
 * Different message types used to differentiate between exchanged messages.
 */
public enum MessageType {

    CREATE_ACCOUNT, TRANSFER, CHECK_BALANCE, PRE_PREPARE, PREPARE, COMMIT, ROUND_CHANGE;

    public boolean isConsensusMessage() {
        return this == PRE_PREPARE || this == PREPARE || this == COMMIT || this == ROUND_CHANGE || this == TRANSFER;
    }

}
