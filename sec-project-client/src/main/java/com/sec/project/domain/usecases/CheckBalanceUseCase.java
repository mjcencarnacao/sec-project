package com.sec.project.domain.usecases;

import com.sec.project.domain.repositories.MessagingService;
import com.sec.project.models.records.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sec.project.models.enums.MessageType.CHECK_BALANCE;

/**
 * Use case describing the behaviour of how a balance request should be performed.
 *
 * @see MessagingService
 */
@Service
public class CheckBalanceUseCase {

    private final MessagingService messagingService;

    @Autowired
    public CheckBalanceUseCase(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void execute(int destination) {
        Message balanceRequest = new Message(CHECK_BALANCE, -1, -1, String.valueOf(-1), destination, -1);
        messagingService.sendMessage(balanceRequest);
        messagingService.receiveResponse();
    }

}
