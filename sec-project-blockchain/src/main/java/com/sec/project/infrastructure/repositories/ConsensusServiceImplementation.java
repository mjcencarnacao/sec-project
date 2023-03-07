package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.models.records.Node;
import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.interfaces.CommandLineInterface;
import com.sec.project.utils.NetworkExchangeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.sec.project.utils.Constants.ROUND_TIMEOUT;

@Service
public class ConsensusServiceImplementation implements ConsensusService {

    private long round = 1;
    private final Node self;
    List<Message> transactions = new LinkedList<>();
    private final NetworkExchangeUtils<Message> networkExchangeUtils;
    private final Logger logger = LoggerFactory.getLogger(CommandLineInterface.class);

    public ConsensusServiceImplementation(Node self, NetworkExchangeUtils<Message> networkExchangeUtils) {
        this.self = self;
        this.networkExchangeUtils = networkExchangeUtils;
    }

    @Override
    @Scheduled(fixedRate = ROUND_TIMEOUT)
    public void start(Message message) {
        if (self.role().isLeader())
            networkExchangeUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
        logger.info(String.format("Starting new Consensus round with id: %d", round++));
    }

    @Override
    public void decide(Message message) {
        transactions.add(message);
        logger.info(String.format("Added message: %s to the blockchain.", message.value()));
    }

    @Override
    public void sendCommitMessage(Message message) {
        networkExchangeUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
        logger.info(String.format("Sent Commit message with value: %s", message.value()));
        decide(message);
    }

    @Override
    public void sendPrepareMessage(Message message) {
        networkExchangeUtils.sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
        logger.info(String.format("Sent Prepare message with value: %s", message.value()));
    }

    @Override
    public void sendPrePrepareMessage(Message message) {
        logger.info(String.format("Sent Pre-Prepare message with value: %s", message.value()));
    }

}
