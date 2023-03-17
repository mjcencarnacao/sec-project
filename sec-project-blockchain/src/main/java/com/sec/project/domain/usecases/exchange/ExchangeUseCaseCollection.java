package com.sec.project.domain.usecases.exchange;

import org.springframework.stereotype.Component;

/**
 * Collection for the use cases relative to key exchange.
 *
 * @param sharePublicKeyUseCase
 * @param shareSecretKeyUseCase
 */
@Component
public record ExchangeUseCaseCollection(SharePublicKeyConsensusUseCase sharePublicKeyUseCase,
                                        ShareSecretKeyConsensusUseCase shareSecretKeyUseCase) {
}
