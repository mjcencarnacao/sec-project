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

import java.util.concurrent.ExecutionException;

import static com.sec.project.domain.models.enums.MessageType.*;

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

    @Override
    public void start() throws ExecutionException, InterruptedException {
        handleMessageTypes(networkUtils.receiveResponse());
        round++;
    }

    @Override
    public void sendCommitMessage(Message received) {
        Message message = new Message(COMMIT, received.id(), round, received.value());
        sendCommitMessageUseCase.execute(message);
    }

    @Override
    public void sendPrepareMessage(Message received) {
        Message message = new Message(PREPARE, received.id(), round, received.value());
        sendPrepareMessageUseCase.execute(message);
        handleMessageTypes(networkUtils.receiveResponse());
    }

    @Override
    public void sendPrePrepareMessage(Message received) {
        Message message = new Message(PRE_PREPARE, received.id(), round, received.value());
        sendPrePrepareMessageUseCase.execute(message);
        handleMessageTypes(networkUtils.receiveResponse());
    }

    @Override
    public void decide() {

    }

    private void handleMessageTypes(Message message) {
        switch (message.type()) {
            case PRE_PREPARE -> sendPrepareMessage(message);
            case PREPARE -> sendCommitMessage(message);
            case COMMIT -> decide();
            default -> sendPrePrepareMessage(message);
        }
    }
}
