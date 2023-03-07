package com.sec.project.domain.usecases;

import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.usecases.UseCase;

public class SendProposeMessageUseCase implements UseCase {

    private final ConsensusService consensusService;

    public SendProposeMessageUseCase(ConsensusService consensusService) {
        this.consensusService = consensusService;
    }

    @Override
    public void execute() {

    }
}
