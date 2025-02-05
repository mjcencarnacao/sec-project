package com.sec.project.domain.models.records;

import com.sec.project.models.records.Message;

import java.util.List;

public record Block(int identifier, long timestamp, List<Message> transactions, String hash) {
    public Block(int identifier, List<Message> transactions, String hash) {
        this(identifier, System.currentTimeMillis(), transactions, hash);
    }
}
