package com.sec.project.domain.usecases;

import org.springframework.stereotype.Component;

@Component
public record UseCaseCollection(SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase,
                                SendPrepareMessageUseCase sendPrepareMessageUseCase,
                                SendCommitMessageUseCase sendCommitMessageUseCase,
                                SendRoundChangeUseCase sendRoundChangeUseCase) {
}
