package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.MessagingService;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MessagingServiceImplementation that follows the defined MessagingService contract.
 *
 * @see MessagingService
 */
@Service
public class MessagingServiceImplementation implements MessagingService {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(MessagingServiceImplementation.class);

    @Autowired
    public MessagingServiceImplementation(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    /**
     * Method that calls the generic NetworkUtils and handles delivery, to the blockchain service, of any type provided.
     *
     * @param message Message to be sent by the client service.
     */
    @Override
    public void sendMessage(Message message) {
        networkUtils.sendMessage(message);
        logger.info("Client sent message with type: " + message.type());
    }

    /**
     * Method that calls the generic NetworkUtils and handles responses from other processes.
     */
    @Override
    public void receiveResponse() {
        Message message = networkUtils.receiveQuorumResponse(Message.class);
        logger.info("Received Quorum of Messages from the Blockchain. Operation Successful. Value: " + message.value());
    }

}
