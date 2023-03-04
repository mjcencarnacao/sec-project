package com.sec.project.domain.models;

import java.io.Serializable;

public record Message(long timestamp, String value) implements Serializable {
    public Message(String value) {
        this(System.currentTimeMillis(), value);
    }
}
