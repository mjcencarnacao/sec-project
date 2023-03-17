package com.sec.project.domain.usecases.consensus;

import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.usecases.UseCase;

/**
 * Contract for the several use cases needed to support the projects functionalities.
 *
 * @see SendRoundChangeConsensusUseCase
 * @see SendCommitMessageConsensusUseCase
 * @see SendPrepareMessageConsensusUseCase
 * @see SendPrePrepareMessageConsensusUseCase
 */
public interface ConsensusUseCase extends UseCase {
    void execute(Message message);
}
