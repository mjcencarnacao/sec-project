package com.sec.project.domain.usecases;

import com.sec.project.domain.usecases.consensus.SendCommitMessageUseCase;
import com.sec.project.domain.usecases.consensus.SendPrePrepareMessageUseCase;
import com.sec.project.domain.usecases.consensus.SendPrepareMessageUseCase;
import com.sec.project.domain.usecases.consensus.SendRoundChangeUseCase;
import com.sec.project.domain.usecases.tes.CheckBalanceUseCase;
import com.sec.project.domain.usecases.tes.CreateAccountUseCase;
import com.sec.project.domain.usecases.tes.TransferUseCase;
import org.springframework.stereotype.Component;

/**
 * Collection for the use cases relative to the IBFT algorithm.
 *
 * @param sendPrePrepareMessageUseCase
 * @param sendPrepareMessageUseCase
 * @param sendCommitMessageUseCase
 * @param sendRoundChangeUseCase
 * @param checkBalanceUseCase
 * @param createAccountUseCase
 * @param transferUseCase
 */
@Component
public record UseCaseCollection(CheckBalanceUseCase checkBalanceUseCase,
                                CreateAccountUseCase createAccountUseCase,
                                TransferUseCase transferUseCase,
                                SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase,
                                SendPrepareMessageUseCase sendPrepareMessageUseCase,
                                SendCommitMessageUseCase sendCommitMessageUseCase,
                                SendRoundChangeUseCase sendRoundChangeUseCase) {
}
