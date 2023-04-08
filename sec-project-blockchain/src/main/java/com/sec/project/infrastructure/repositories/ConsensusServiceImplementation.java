package com.sec.project.infrastructure.repositories;

import com.google.gson.Gson;
import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.domain.models.records.Queue;
import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.usecases.SendCommitMessageUseCase;
import com.sec.project.domain.usecases.SendPrePrepareMessageUseCase;
import com.sec.project.domain.usecases.SendPrepareMessageUseCase;
import com.sec.project.domain.usecases.UseCaseCollection;
import com.sec.project.utils.NetworkUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sec.project.domain.models.enums.MessageType.*;

/**
 * ConsensusServiceImplementation that follows the defined ConsensusService contract.
 *
 * @see ConsensusService
 */
@Service
public class ConsensusServiceImplementation implements ConsensusService {

    private int clientPort;
    private long round = 1;
    private final Gson gson;
    private final NetworkUtils<Message> networkUtils;
    private final UseCaseCollection useCaseCollection;
    public static final Queue blockchainTransactions = new Queue();
    private final Logger logger = LoggerFactory.getLogger(ConsensusServiceImplementation.class);

    @Autowired
    public ConsensusServiceImplementation(Gson gson, NetworkUtils<Message> networkUtils, UseCaseCollection useCaseCollection) {
        this.gson = gson;
        this.networkUtils = networkUtils;
        this.useCaseCollection = useCaseCollection;
    }

    /**
     * Method that handles the starting logic to initiate an IBFT round.
     * Waits for a client message and sends it to the message handler defined in the service.
     */
    @Override
    public void start(Optional<Message> message) {
        logger.info("Starting new round for the IBFT algorithm.");
        if (message.isPresent())
            handleMessageTypes(message.get());
        else {
            ImmutablePair<Integer, MessageTransferObject> responseObject = networkUtils.receiveResponse();
            Message response = gson.fromJson(new String(responseObject.right.data()).trim(), Message.class);
            clientPort = responseObject.left;
            handleMessageTypes(response);
        }
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
        useCaseCollection.sendCommitMessageUseCase().execute(new Message(COMMIT, received.id(), round, received.value()));
        handleMessageTypes(networkUtils.receiveQuorumResponse(Message.class));
    }

    /**
     * Method that handles the delivery of the prepared messages to ensure the IBFT protocol integrity.
     *
     * @param received previous pre-prepare message.
     * @see SendPrepareMessageUseCase
     */
    @Override
    public void sendPrepareMessage(Message received) {
        useCaseCollection.sendPrepareMessageUseCase().execute(new Message(PREPARE, received.id(), round, received.value()));
        handleMessageTypes(networkUtils.receiveQuorumResponse(Message.class));
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
        useCaseCollection.sendPrePrepareMessageUseCase().execute(message);
        MessageTransferObject response = networkUtils.receiveResponse().right;
        handleMessageTypes(gson.fromJson(new String(response.data()).trim(), Message.class));
    }

    /**
     * External method that appends a given value to the blockchain after exchanging the correct amount of messages
     * in the IBFT protocol.
     * After appending waits for new messages from the Client side.
     */
    @Override
    public void decide(Message message) {
        blockchainTransactions.queue().add(message);
        logger.info("Added to the blockchain with value: " + message.value());
        networkUtils.sendMessage(message, SendingMethod.UNICAST, Optional.of(clientPort));
        round = 1;
        start(Optional.empty());
    }

    /**
     * Handles different message types that can take place in the IBFT protocol.
     *
     * @param message to analyze and handle.
     */
    private void handleMessageTypes(Message message) {
        if (message == null)
            start(Optional.empty());
        else if (message.type() == null)
            sendPrePrepareMessage(new Message(null, blockchainTransactions.queue().size(), round, message.value()));
        else
            switch (message.type()) {
                case PRE_PREPARE -> sendPrepareMessage(message);
                case PREPARE -> sendCommitMessage(message);
                case COMMIT -> decide(message);
            }
    }
}
