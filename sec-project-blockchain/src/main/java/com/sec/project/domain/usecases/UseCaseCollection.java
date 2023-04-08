package com.sec.project.domain.usecases;

import org.springframework.stereotype.Component;

/**
 * Collection for the use cases relative to the IBFT algorithm.
 *
 * @param sendPrePrepareMessageUseCase
 * @param sendPrepareMessageUseCase
 * @param sendCommitMessageUseCase
 * @param sendRoundChangeUseCase
 */
@Component
public record UseCaseCollection(SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase,
                                SendPrepareMessageUseCase sendPrepareMessageUseCase,
                                SendCommitMessageUseCase sendCommitMessageUseCase,
                                SendRoundChangeUseCase sendRoundChangeUseCase) {
}
