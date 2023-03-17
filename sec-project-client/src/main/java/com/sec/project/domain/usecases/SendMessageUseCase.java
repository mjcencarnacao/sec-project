package com.sec.project.domain.usecases;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Use case describing the behaviour on how a message should be sent by invoking the MessagingService.
 *
 * @see MessagingService
 */
@Service
public class SendMessageUseCase {

    private final MessagingService messagingService;

    @Autowired
    public SendMessageUseCase(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void execute(Message message) {
        messagingService.sendMessage(message);
    }

}
