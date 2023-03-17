package com.sec.project.domain.models.enums;

/**
 * Different message types used to differentiate between exchanged messages in the IBFT protocol.
 */
public enum MessageType {
    PRE_PREPARE, PREPARE, COMMIT, ROUND_CHANGE
}
