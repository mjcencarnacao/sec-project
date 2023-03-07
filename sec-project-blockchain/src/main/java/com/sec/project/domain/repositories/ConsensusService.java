package com.sec.project.domain.repositories;

import com.sec.project.domain.models.records.Message;
import org.springframework.stereotype.Service;

@Service
public interface ConsensusService {

    void start(Message message);

    void decide(Message message);

    void sendCommitMessage(Message message);

    void sendPrepareMessage(Message message);

    void sendPrePrepareMessage(Message message);

}
