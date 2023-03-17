package com.sec.project.infrastructure.repositories;

import com.google.gson.Gson;
import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.domain.models.records.Queue;
import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.repositories.KeyExchangeService;
import com.sec.project.domain.usecases.consensus.ConsensusUseCaseCollection;
import com.sec.project.domain.usecases.consensus.SendCommitMessageConsensusUseCase;
import com.sec.project.domain.usecases.consensus.SendPrePrepareMessageConsensusUseCase;
import com.sec.project.domain.usecases.consensus.SendPrepareMessageConsensusUseCase;
import com.sec.project.infrastructure.annotations.FlushUDPBuffer;
import com.sec.project.utils.NetworkUtils;
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

    private long round = 1;
    private final Gson gson;
    private final NetworkUtils<Message> networkUtils;
    private final KeyExchangeService keyExchangeService;
    private final Queue blockchainTransactions = new Queue();
    private final ConsensusUseCaseCollection useCaseCollection;
    private final Logger logger = LoggerFactory.getLogger(ConsensusServiceImplementation.class);

    @Autowired
    public ConsensusServiceImplementation(Gson gson, NetworkUtils<Message> networkUtils, ConsensusUseCaseCollection useCaseCollection, KeyExchangeService keyExchangeService) {
        this.gson = gson;
        this.networkUtils = networkUtils;
        this.useCaseCollection = useCaseCollection;
        this.keyExchangeService = keyExchangeService;
    }

    /**
     * Method that handles the starting logic to initiate an IBFT round.
     * Waits for a client message and sends it to the message handler defined in the service.
     */
    @Override
    @FlushUDPBuffer(override = true)
    public void start(Optional<Message> message) {
        logger.info("Starting new round for the IBFT algorithm.");
        if (message.isPresent())
            handleMessageTypes(message.get());
        else {
            Message response = gson.fromJson(new String(networkUtils.receiveResponse(true).right.data()).trim(), Message.class);
            keyExchangeService.exchangeKeys();
            handleMessageTypes(response);
        }
        round++;
    }

    /**
     * Method that handles the delivery of the commit messages to ensure the IBFT protocol integrity.
     * After everything is committed a proper decide will take place to ensure the blockchain appends (or not) a given message.
     *
     * @param received previous prepare message.
     * @see SendCommitMessageConsensusUseCase
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
     * @see SendPrepareMessageConsensusUseCase
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
     * @see SendPrePrepareMessageConsensusUseCase
     */
    @Override
    public void sendPrePrepareMessage(Message received) {
        Message message = new Message(PRE_PREPARE, received.id(), round, received.value());
        useCaseCollection.sendPrePrepareMessageUseCase().execute(message);
        MessageTransferObject response = networkUtils.receiveResponse(true).right;
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
        round = 1;
        start(Optional.empty());
    }

    /**
     * Handles different message types that can take place in the IBFT protocol.
     *
     * @param message to analyze and handle.
     */
    private void handleMessageTypes(Message message) {
        if (message.type() == null)
            sendPrePrepareMessage(new Message(null, blockchainTransactions.queue().size(), round, message.value()));
        else
            switch (message.type()) {
                case PRE_PREPARE -> sendPrepareMessage(message);
                case PREPARE -> sendCommitMessage(message);
                case COMMIT -> decide(message);
            }
    }
}
