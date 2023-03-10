package com.sec.project.domain.models;

import java.io.Serializable;

/**
 * Message object record containing the timestamp of its creation and the value to be sent to the blockchain.
 *
 * @param timestamp Message creation time.
 * @param value     Value to be appended to the blockchain service.
 */
public record Message(long timestamp, String value) implements Serializable {
    public Message(String value) {
        this(System.currentTimeMillis(), value);
    }
}
