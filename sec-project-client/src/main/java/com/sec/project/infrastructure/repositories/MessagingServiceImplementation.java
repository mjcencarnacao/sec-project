package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingServiceImplementation implements MessagingService {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(MessagingServiceImplementation.class);

    @Autowired
    public MessagingServiceImplementation(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    @Override
    public void sendMessage(Message message) {
        networkUtils.sendMessage(message);
        logger.info("Client sent message with value: " + message.value());
    }

    @Override
    public void receiveResponse() {
        Message message = networkUtils.receiveResponse(Message.class);
        logger.info("Client received message with value: " + message.value());
    }

}
