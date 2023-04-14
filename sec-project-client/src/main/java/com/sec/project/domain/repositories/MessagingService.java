package com.sec.project.domain.repositories;

import com.sec.project.models.enums.ReadType;
import com.sec.project.models.records.Message;

/**
 * Contract for the MessagingServiceImplementation where the message exchange will take place.
 *
 * @see com.sec.project.infrastructure.repositories.MessagingServiceImplementation
 */
public interface MessagingService {

    /**
     * Contract method to implement the logic to receive a response from the blockchain service.
     */
    void receiveResponse(boolean unicast);

    /**
     * Contract method to implement the logic to send a message to the blockchain service.
     *
     * @param message Message to be sent by the client service.
     */
    void sendMessage(Message message, ReadType readType);
}
