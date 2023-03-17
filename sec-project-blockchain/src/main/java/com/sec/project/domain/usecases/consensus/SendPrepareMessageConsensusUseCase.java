package com.sec.project.domain.usecases.consensus;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.infrastructure.annotations.Byzantine;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use case describing the behaviour to send a prepared message by invoking the NetworkUtils logic.
 *
 * @see NetworkUtils
 */
@Service
public class SendPrepareMessageConsensusUseCase implements ConsensusUseCase {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(SendPrepareMessageConsensusUseCase.class);

    @Autowired
    public SendPrepareMessageConsensusUseCase(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    @Override
    @Byzantine
    public void execute(Message message) {
        networkUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty(), true);
        logger.info(String.format("Member sent a Prepare for message with ID: %d", message.id()));
    }
}
