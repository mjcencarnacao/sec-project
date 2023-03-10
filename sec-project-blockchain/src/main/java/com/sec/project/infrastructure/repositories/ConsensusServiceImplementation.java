package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.usecases.SendCommitMessageUseCase;
import com.sec.project.domain.usecases.SendPrePrepareMessageUseCase;
import com.sec.project.domain.usecases.SendPrepareMessageUseCase;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sec.project.domain.models.enums.MessageType.*;

/**
 * ConsensusServiceImplementation that follows the defined ConsensusService contract.
 *
 * @see ConsensusService
 */
@Service
public class ConsensusServiceImplementation implements ConsensusService {

    private long round = 1;
    private final NetworkUtils<Message> networkUtils;
    private final SendCommitMessageUseCase sendCommitMessageUseCase;
    private final SendPrepareMessageUseCase sendPrepareMessageUseCase;
    private final SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase;
    private final Logger logger = LoggerFactory.getLogger(ConsensusServiceImplementation.class);

    @Autowired
    public ConsensusServiceImplementation(SendCommitMessageUseCase sendCommitMessageUseCase, SendPrepareMessageUseCase sendPrepareMessageUseCase, SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase, NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
        this.sendCommitMessageUseCase = sendCommitMessageUseCase;
        this.sendPrepareMessageUseCase = sendPrepareMessageUseCase;
        this.sendPrePrepareMessageUseCase = sendPrePrepareMessageUseCase;
    }

    /**
     * Method that handles the starting logic to initiate an IBFT round.
     * Waits for a client message and sends it to the message handler defined in the service.
     */
    @Override
    public void start() {
        handleMessageTypes(networkUtils.receiveResponse(Message.class));
        round++;
    }

    /**
     * Method that handles the delivery of the commit messages to ensure the IBFT protocol integrity.
     * After everything is committed a proper decide will take place to ensure the blockchain appends (or not) a given message.
     *
     * @param received previous prepare message.
     * @see SendCommitMessageUseCase
     */
    @Override
    public void sendCommitMessage(Message received) {
        Message message = new Message(COMMIT, received.id(), round, received.value());
        sendCommitMessageUseCase.execute(message);
    }

    /**
     * Method that handles the delivery of the prepared messages to ensure the IBFT protocol integrity.
     *
     * @param received previous pre-prepare message.
     * @see SendPrepareMessageUseCase
     */
    @Override
    public void sendPrepareMessage(Message received) {
        Message message = new Message(PREPARE, received.id(), round, received.value());
        sendPrepareMessageUseCase.execute(message);
        handleMessageTypes(networkUtils.receiveQuorumResponse(Message.class).get(0));
    }

    /**
     * Method that handles the delivery of the pre-prepare messages to ensure the IBFT protocol integrity.
     * Only the Leader can execute this (specified in the use case).
     *
     * @param received client message request.
     * @see SendPrePrepareMessageUseCase
     */
    @Override
    public void sendPrePrepareMessage(Message received) {
        Message message = new Message(PRE_PREPARE, received.id(), round, received.value());
        sendPrePrepareMessageUseCase.execute(message);
        handleMessageTypes(networkUtils.receiveQuorumResponse(Message.class).get(0));
    }

    /**
     * External method that appends a given value to the blockchain after exchanging the correct amount of messages
     * in the IBFT protocol.
     */
    @Override
    public void decide() {

    }

    /**
     * Handles different message types that can take place in the IBFT protocol.
     *
     * @param message to analyze and handle.
     */
    private void handleMessageTypes(Message message) {
        if (message.type() == null) message = new Message(PRE_PREPARE, message.timestamp(), round, message.value());
        switch (message.type()) {
            case PRE_PREPARE -> sendPrepareMessage(message);
            case PREPARE -> sendCommitMessage(message);
            case COMMIT -> decide();
            default -> sendPrePrepareMessage(message);
        }
    }
}
