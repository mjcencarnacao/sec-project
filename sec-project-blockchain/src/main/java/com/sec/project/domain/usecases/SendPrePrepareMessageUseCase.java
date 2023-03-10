package com.sec.project.domain.usecases;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.infrastructure.annotations.Byzantine;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sec.project.interfaces.CommandLineInterface.self;

@Service
public class SendPrePrepareMessageUseCase implements UseCase {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(SendPrePrepareMessageUseCase.class);

    @Autowired
    public SendPrePrepareMessageUseCase(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    @Override
    @Byzantine
    public void execute(Message message) {
        if (self.getRole().isLeader()) {
            networkUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
            logger.info(String.format("Leader sent Pre-Prepare request for message with ID: %d", message.id()));
        }
    }
}
