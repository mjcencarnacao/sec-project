package com.sec.project.domain.usecases;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sec.project.domain.enums.MessageType.CREATE_ACCOUNT;
import static com.sec.project.utils.NetworkUtils.connection;

/**
 * Use case describing the behaviour of how an account request should be performed.
 *
 * @see MessagingService
 */
@Service
public class CreateAccountUseCase {

    private final MessagingService messagingService;

    @Autowired
    public CreateAccountUseCase(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void execute() {
        Message creationRequest = new Message(CREATE_ACCOUNT, 0, connection.datagramSocket().getLocalPort(), -1);
        messagingService.sendMessage(creationRequest);
    }

}
