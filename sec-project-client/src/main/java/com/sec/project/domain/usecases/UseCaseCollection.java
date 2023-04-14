package com.sec.project.domain.usecases;

import org.springframework.stereotype.Component;

/**
 * Use Case Collection for Client Operations.
 *
 * @param checkBalanceUseCase
 * @param createAccountUseCase
 * @param transferUseCase
 */
@Component
public record UseCaseCollection(CheckBalanceUseCase checkBalanceUseCase,
                                CreateAccountUseCase createAccountUseCase,
                                TransferUseCase transferUseCase) {
}
