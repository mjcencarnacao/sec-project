package com.sec.project.domain.usecases;

import com.sec.project.domain.repositories.ConsensusService;
import com.sec.project.domain.usecases.UseCase;

public class SendFinishMessageUseCase implements UseCase {

    private final ConsensusService consensusService;

    public SendFinishMessageUseCase(ConsensusService consensusService) {
        this.consensusService = consensusService;
    }

    @Override
    public void execute() {

    }
}
