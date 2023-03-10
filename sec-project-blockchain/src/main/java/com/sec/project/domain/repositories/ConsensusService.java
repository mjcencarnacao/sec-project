package com.sec.project.domain.repositories;

import com.sec.project.domain.models.records.Message;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * Contract for the ConsensusServiceImplementation where the IBFT message exchange will take place.
 *
 * @see com.sec.project.infrastructure.repositories.ConsensusServiceImplementation
 */
@Service
public interface ConsensusService {

    void start() throws ExecutionException, InterruptedException;

    void decide();

    void sendCommitMessage(Message message);

    void sendPrepareMessage(Message message);

    void sendPrePrepareMessage(Message message);

}
