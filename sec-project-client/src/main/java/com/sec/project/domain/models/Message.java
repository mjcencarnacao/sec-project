package com.sec.project.domain.models;

import com.sec.project.domain.enums.MessageType;

/**
 * Message object record containing the timestamp of its creation and the value to be sent to the blockchain.
 *
 * @param value Value to be appended to the blockchain service.
 */
public record Message(MessageType type, int value, int source, int destination) {
}
