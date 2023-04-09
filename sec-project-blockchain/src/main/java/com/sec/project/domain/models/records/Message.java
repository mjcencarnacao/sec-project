package com.sec.project.domain.models.records;

import com.sec.project.domain.models.enums.MessageType;

/**
 * Message object record containing the different types of field required to be sent to the blockchain.
 *
 * @param type  message type, indicating the context of the communication-
 * @param id    for the specific message.
 * @param round of the IBFT consensus algorithm.
 * @param value to be appended to the blockchain.
 */
public record Message(MessageType type, long id, long round, String value, int source, int destination) {
}
