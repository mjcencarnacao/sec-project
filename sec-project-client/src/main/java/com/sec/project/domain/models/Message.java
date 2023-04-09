package com.sec.project.domain.models;

import com.sec.project.domain.enums.MessageType;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Optional;

/**
 * Message object record containing the timestamp of its creation and the value to be sent to the blockchain.
 *
 * @param value     Value to be appended to the blockchain service.
 */
public record Message(MessageType type, String value, int source, int destination) {
}
