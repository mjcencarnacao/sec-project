package com.sec.project.domain.usecases;

import com.sec.project.domain.repositories.ConsensusService;

public class SendCommitMessageUseCase implements UseCase {

    private final ConsensusService consensusService;

    public SendCommitMessageUseCase(ConsensusService consensusService) {
        this.consensusService = consensusService;
    }

    @Override
    public void execute() {

    }
}
