package com.sec.project.domain.models.records;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Queue record object that allows for the tracking of the messages received and messages appended.
 *
 * @param queue of messages to be processed by the IBFT protocol. Preserves message order.
 */
@Component
public record Queue(List<Message> queue) {
    public Queue() {
        this(new LinkedList<>());
    }
}
