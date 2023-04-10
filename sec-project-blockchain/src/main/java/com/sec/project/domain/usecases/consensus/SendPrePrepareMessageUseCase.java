package com.sec.project.domain.usecases.consensus;

import com.sec.project.domain.usecases.UseCase;
import com.sec.project.models.enums.SendingMethod;
import com.sec.project.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sec.project.interfaces.CommandLineInterface.self;

/**
 * Use case describing the behaviour to send a pre-prepared message by invoking the NetworkUtils logic.
 * Only a node that is a Leader can perform this operation.
 *
 * @see NetworkUtils
 */
@Service
public class SendPrePrepareMessageUseCase implements UseCase {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(SendPrePrepareMessageUseCase.class);

    @Autowired
    public SendPrePrepareMessageUseCase(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    @Override
    public void execute(Message message) {
        if (self.getRole().isLeader()) {
            networkUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
            logger.info(String.format("Leader sent Pre-Prepare request for message with ID: %d", message.id()));
        }
    }
}
