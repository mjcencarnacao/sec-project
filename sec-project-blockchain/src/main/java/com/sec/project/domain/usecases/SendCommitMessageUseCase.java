package com.sec.project.domain.usecases;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SendCommitMessageUseCase implements UseCase {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(SendPrepareMessageUseCase.class);

    @Autowired
    public SendCommitMessageUseCase(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    @Override
    public void execute(Message message) {
        networkUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
        logger.info(String.format("Member sent a Commit for message with ID: %d", message.id()));
    }
}
