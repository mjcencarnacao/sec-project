package com.sec.project.domain.usecases;

import com.sec.project.domain.repositories.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiveResponseUseCase {

    private final MessagingService messagingService;

    @Autowired
    public ReceiveResponseUseCase(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void execute() {
        messagingService.receiveResponse();
    }
}
