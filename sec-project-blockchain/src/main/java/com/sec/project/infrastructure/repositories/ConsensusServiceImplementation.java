package com.sec.project.infrastructure.repositories;

import com.google.gson.Gson;
import com.sec.project.configuration.SecurityConfiguration;
import com.sec.project.domain.models.records.Block;
import com.sec.project.domain.models.records.Queue;
import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.usecases.UseCaseCollection;
import com.sec.project.domain.usecases.consensus.SendCommitMessageUseCase;
import com.sec.project.domain.usecases.consensus.SendPrePrepareMessageUseCase;
import com.sec.project.domain.usecases.consensus.SendPrepareMessageUseCase;
import com.sec.project.models.enums.SendingMethod;
import com.sec.project.models.records.Message;
import com.sec.project.models.records.MessageTransferObject;
import com.sec.project.utils.NetworkUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sec.project.configuration.StaticNodeConfiguration.LEADER_PORT;
import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.models.enums.MessageType.*;
import static com.sec.project.utils.Constants.MINIMUM_TRANSACTIONS;

/**
 * ConsensusServiceImplementation that follows the defined ConsensusService contract.
 *
 * @see ConsensusService
 */
@Service
public class ConsensusServiceImplementation implements ConsensusService {

    private long round = 1;
    private final Gson gson;
    public static int clientPort;
    private final NetworkUtils<Message> networkUtils;
    private final UseCaseCollection useCaseCollection;
    private final SecurityConfiguration securityConfiguration;
    public static final Queue blockchainTransactions = new Queue();
    private final Logger logger = LoggerFactory.getLogger(ConsensusServiceImplementation.class);

    @Autowired
    public ConsensusServiceImplementation(Gson gson, SecurityConfiguration securityConfiguration, NetworkUtils<Message> networkUtils, UseCaseCollection useCaseCollection) {
        this.gson = gson;
        this.networkUtils = networkUtils;
        this.useCaseCollection = useCaseCollection;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Method that handles the starting logic to initiate an IBFT round.
     * Waits for a client message and sends it to the message handler defined in the service.
     */
    @Override
    public void start(Optional<Message> message) {
        logger.info("Starting new round for the IBFT algorithm.");
        new Thread(networkUtils::enqueueClientRequests).start();
        if (message.isPresent())
            handleMessageTypes(message.get());
        else {
            while (true) {
                if (!blockchainTransactions.clientRequests().isEmpty()) {
                    ImmutablePair<Integer, MessageTransferObject> responseObject = blockchainTransactions.clientRequests().get(0);
                    Message response = gson.fromJson(new String(responseObject.right.data()).trim(), Message.class);
                    clientPort = responseObject.left;
                    if (getPublicKeysFromFile(true).get(response.source()) != null && securityConfiguration.verifySignature(getPublicKeysFromFile(true).get(response.source()), responseObject.right.data(), responseObject.right.signature()))
                        handleMessageTypes(response);
                    else
                        blockchainTransactions.clientRequests().remove(0);
                }
            }
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
        useCaseCollection.sendCommitMessageUseCase().execute(new Message(COMMIT, received.id(), round, received.value(), -1, -1));
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
        useCaseCollection.sendPrepareMessageUseCase().execute(new Message(PREPARE, received.id(), round, received.value(), -1, -1));
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
        useCaseCollection.sendPrePrepareMessageUseCase().execute(new Message(PRE_PREPARE, received.id(), round, received.value(), -1, -1));
        ImmutablePair<Integer, MessageTransferObject> response = networkUtils.receiveResponse();
        if (response.left == LEADER_PORT)
            handleMessageTypes(gson.fromJson(new String(response.right.data()).trim(), Message.class));
        else
            start(Optional.empty());
    }

    /**
     * External method that appends a given value to the blockchain after exchanging the correct amount of messages
     * in the IBFT protocol.
     * After appending waits for new messages from the Client side.
     */
    @Override
    public void decide(Message message) {
        round = 1;
        logger.info("Added new block to the blockchain with ID: " + blockchainTransactions.transactions().size());
        blockchainTransactions.transactions().add(gson.fromJson(message.value(), Block.class));
        networkUtils.sendMessage(message, SendingMethod.UNICAST, Optional.of(clientPort));
        start(Optional.empty());
    }

    /**
     * Handles transfer messages and sends them to consensus if a given target is reached.
     *
     * @param message containing the information about the transfer.
     */
    private void handleTransferMessage(Message message) {
        useCaseCollection.transferUseCase().execute(message);
        if (blockchainTransactions.queue().size() == MINIMUM_TRANSACTIONS) {
            Block block = new Block(blockchainTransactions.transactions().size(), blockchainTransactions.queue(), securityConfiguration.generateMessageDigest(gson.toJson(blockchainTransactions.queue()).getBytes()));
            blockchainTransactions.queue().clear();
            sendPrePrepareMessage(new Message(null, blockchainTransactions.transactions().size(), round, gson.toJson(block), -1, -1));
        }
    }

    /**
     * Handles different message types that can take place in the IBFT protocol.
     *
     * @param message to analyze and handle.
     */
    private void handleMessageTypes(Message message) {
        switch (message.type()) {
            case COMMIT -> decide(message);
            case PREPARE -> sendCommitMessage(message);
            case PRE_PREPARE -> sendPrepareMessage(message);
            case TRANSFER -> handleTransferMessage(message);
            case CHECK_BALANCE -> useCaseCollection.checkBalanceUseCase().execute(message);
            case CREATE_ACCOUNT -> useCaseCollection.createAccountUseCase().execute(message);
        }
    }
}
