package com.sec.project.domain.repositories;

import com.sec.project.domain.models.Message;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface MessagingService {
    @Async
    CompletableFuture<Message> receiveResponse();

    @Async
    void sendMessage(Message message) throws IOException;
}
