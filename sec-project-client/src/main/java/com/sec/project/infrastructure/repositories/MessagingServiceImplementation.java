package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.MessagingService;
import com.sec.project.models.enums.ReadType;
import com.sec.project.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sec.project.utils.NetworkUtils.connection;

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
    public void sendMessage(Message message, ReadType readType) {
        networkUtils.sendMessage(message, readType);
        logger.info("Client sent message with type: " + message.type());
    }

    /**
     * Method that calls the generic NetworkUtils and handles responses from other processes.
     */
    @Override
    public void receiveResponse(boolean unicast) {
        Message message = unicast ? networkUtils.receiveResponse(Message.class) : networkUtils.receiveQuorumResponse(Message.class);
        logger.info("Received Response from the Blockchain. Operation Successful. Value: " + message.value());
    }

}
