package com.sec.project.domain.models.records;

import com.sec.project.domain.models.enums.MessageType;

import java.io.Serializable;

public record Message(MessageType type, long id, long round, long timestamp, String value) implements Serializable {
    public Message(MessageType type, long id, long round, String value) {
        this(type, id, round, System.currentTimeMillis(), value);
    }
}
