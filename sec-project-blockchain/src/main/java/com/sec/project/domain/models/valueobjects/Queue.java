package com.sec.project.domain.models.valueobjects;

import com.sec.project.domain.models.records.Message;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public record Queue(List<Message> queue) {
    public Queue() {
        this(new LinkedList<>());
    }
}
