package com.sec.project.infrastructure.repositories;

import com.google.gson.Gson;
import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Block;
import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.domain.models.records.Queue;
import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.repositories.TokenExchangeSystemService;
import com.sec.project.domain.usecases.SendCommitMessageUseCase;
import com.sec.project.domain.usecases.SendPrePrepareMessageUseCase;
import com.sec.project.domain.usecases.SendPrepareMessageUseCase;
import com.sec.project.domain.usecases.UseCaseCollection;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.utils.NetworkUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.security.PublicKey;
import java.util.Optional;

import static com.sec.project.domain.models.enums.MessageType.*;
import static com.sec.project.infrastructure.configuration.StaticNodeConfiguration.LEADER_PORT;
import static com.sec.project.infrastructure.configuration.StaticNodeConfiguration.getPublicKeysOfClientFromFile;
import static com.sec.project.interfaces.CommandLineInterface.clientListener;
import static com.sec.project.utils.Constants.MAX_BUFFER_SIZE;

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
    private final SecurityConfiguration securityConfiguration;
    private final TokenExchangeSystemService tokenExchangeSystemService;
    public static final Queue blockchainTransactions = new Queue();
    private final Logger logger = LoggerFactory.getLogger(ConsensusServiceImplementation.class);

    @Autowired
    public ConsensusServiceImplementation(Gson gson, SecurityConfiguration securityConfiguration, TokenExchangeSystemService tokenExchangeSystemService, NetworkUtils<Message> networkUtils, UseCaseCollection useCaseCollection) {
        this.gson = gson;
        this.networkUtils = networkUtils;
        this.useCaseCollection = useCaseCollection;
        this.securityConfiguration = securityConfiguration;
        this.tokenExchangeSystemService = tokenExchangeSystemService;
    }

    /**
     * Method that handles the starting logic to initiate an IBFT round.
     * Waits for a client message and sends it to the message handler defined in the service.
     */
    @Override
    public void start(Optional<Message> message) {
        logger.info("Starting new round for the IBFT algorithm.");
        new Thread(this::enqueueClientRequests).start();
        if (message.isPresent())
            handleMessageTypes(message.get());
        else {
            while (true) {
                if (!blockchainTransactions.clientRequests().isEmpty()) {
                    ImmutablePair<Integer, MessageTransferObject> responseObject = blockchainTransactions.clientRequests().get(0);
                    Message response = gson.fromJson(new String(responseObject.right.data()).trim(), Message.class);
                    clientPort = responseObject.left;
                    handleMessageTypes(response);
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
        useCaseCollection.sendCommitMessageUseCase().execute(new Message(COMMIT, received.id(), round, received.value(), 0, 0));
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
        useCaseCollection.sendPrepareMessageUseCase().execute(new Message(PREPARE, received.id(), round, received.value(), 0, 0));
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
        Message message = new Message(PRE_PREPARE, received.id(), round, received.value(), 0, 0);
        useCaseCollection.sendPrePrepareMessageUseCase().execute(message);
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
        //blockchainTransactions.queue().add(message);
        logger.info("Added new block with ID: " + blockchainTransactions.transactions().size());
        blockchainTransactions.transactions().add(gson.fromJson(message.value(), Block.class));
        networkUtils.sendMessage(message, SendingMethod.UNICAST, Optional.of(clientPort));
        round = 1;
        start(Optional.empty());
    }

    public void createAccount(Message message) {
        tokenExchangeSystemService.createAccount(message.source());
        blockchainTransactions.clientRequests().remove(0);
    }

    public void transfer(Message message) {
        PublicKey source = getPublicKeysOfClientFromFile().get(message.source());
        PublicKey destination = getPublicKeysOfClientFromFile().get(message.destination());
        tokenExchangeSystemService.transfer(source, destination, Integer.parseInt(message.value()));
    }

    public void checkBalance(Message message) {
        PublicKey source = getPublicKeysOfClientFromFile().get(message.source());
        networkUtils.sendMessage(new Message(CHECK_BALANCE, 0, 0, String.valueOf(tokenExchangeSystemService.check_balance(source)), 0, 0), SendingMethod.UNICAST, Optional.of(clientPort));
        blockchainTransactions.clientRequests().remove(0);
    }

    private void enqueueClientRequests() {
        while (true) {
            try {
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
                DatagramPacket dataReceived = new DatagramPacket(buffer, buffer.length);
                clientListener.receive(dataReceived);
                MessageTransferObject message = gson.fromJson(new String(buffer).trim(), MessageTransferObject.class);
                blockchainTransactions.clientRequests().add(new ImmutablePair<>(dataReceived.getPort(), message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Handles different message types that can take place in the IBFT protocol.
     *
     * @param message to analyze and handle.
     */
    private void handleMessageTypes(Message message) {
        if (message == null)
            start(Optional.empty());
        else if (message.type() == TRANSFER) {
            blockchainTransactions.queue().add(message);
            transfer(message);
            logger.info("Added to Queue");
            blockchainTransactions.clientRequests().remove(0);
            if (blockchainTransactions.queue().size() == 5) {
                Block block = new Block(blockchainTransactions.transactions().size(), blockchainTransactions.queue(), securityConfiguration.generateMessageDigest(gson.toJson(blockchainTransactions.queue()).getBytes()));
                blockchainTransactions.queue().clear();
                String bytes = gson.toJson(block);
                sendPrePrepareMessage(new Message(null, blockchainTransactions.transactions().size(), round, bytes, 0, 0));
            }
        } else
            switch (message.type()) {
                case CREATE_ACCOUNT -> createAccount(message);
                case CHECK_BALANCE -> checkBalance(message);
                case PRE_PREPARE -> sendPrepareMessage(message);
                case PREPARE -> sendCommitMessage(message);
                case COMMIT -> decide(message);
            }
    }
}
