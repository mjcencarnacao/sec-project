package com.sec.project.domain.usecases.exchange;

import com.sec.project.domain.usecases.UseCase;

/**
 * Contract for the several use cases needed to support the projects functionalities.
 *
 * @see SharePublicKeyConsensusUseCase
 * @see ShareSecretKeyConsensusUseCase
 */
public interface ExchangeUseCase extends UseCase {
    void execute();
}
