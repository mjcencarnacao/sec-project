package com.sec.project.domain.usecases.consensus;

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
public record ConsensusUseCaseCollection(SendPrePrepareMessageConsensusUseCase sendPrePrepareMessageUseCase,
                                         SendPrepareMessageConsensusUseCase sendPrepareMessageUseCase,
                                         SendCommitMessageConsensusUseCase sendCommitMessageUseCase,
                                         SendRoundChangeConsensusUseCase sendRoundChangeUseCase) {
}
