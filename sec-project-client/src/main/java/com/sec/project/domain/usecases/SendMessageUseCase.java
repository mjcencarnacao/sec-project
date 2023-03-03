package com.sec.project.domain.usecases;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendMessageUseCase {

    private final MessagingService messagingService;

    public SendMessageUseCase(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void execute(Message message) throws IOException {
        messagingService.sendMessage(message);
    }

}
