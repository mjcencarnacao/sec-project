package com.sec.project.domain.usecases.consensus;

import com.sec.project.domain.usecases.UseCase;
import com.sec.project.infrastructure.annotations.Byzantine;
import com.sec.project.models.enums.SendingMethod;
import com.sec.project.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use case describing the behaviour on how a commit message should be sent by invoking the NetworkUtils logic.
 *
 * @see NetworkUtils
 */
@Service
public class SendCommitMessageUseCase implements UseCase {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(SendPrepareMessageUseCase.class);

    @Autowired
    public SendCommitMessageUseCase(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    @Override
    @Byzantine
    public void execute(Message message) {
        networkUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
        logger.info(String.format("Member sent a Commit for message with ID: %d", message.id()));
    }
}
