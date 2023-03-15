package com.sec.project.domain.usecases.consensus;

import org.springframework.stereotype.Component;

@Component
public record ConsensusUseCaseCollection(SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase,
                                         SendPrepareMessageUseCase sendPrepareMessageUseCase,
                                         SendCommitMessageUseCase sendCommitMessageUseCase,
                                         SendRoundChangeUseCase sendRoundChangeUseCase) {
}
