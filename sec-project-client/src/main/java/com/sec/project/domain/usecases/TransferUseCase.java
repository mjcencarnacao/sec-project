package com.sec.project.domain.usecases;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sec.project.domain.enums.MessageType.TRANSFER;
import static com.sec.project.utils.NetworkUtils.connection;

/**
 * Use case describing the behaviour of how a transfer should take place.
 *
 * @see MessagingService
 */
@Service
public class TransferUseCase {

    private final MessagingService messagingService;

    @Autowired
    public TransferUseCase(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void execute(int destination, int amount) {
        Message transferMessage = new Message(TRANSFER, amount, connection.datagramSocket().getLocalPort(), destination);
        messagingService.sendMessage(transferMessage);
        messagingService.receiveResponse();
    }

}
