package com.sec.project.domain.usecases.exchange;

import org.springframework.stereotype.Component;

@Component
public record ExchangeUseCaseCollection(SharePublicKeyUseCase sharePublicKeyUseCase,
                                        ShareSecretKeyUseCase shareSecretKeyUseCase) {
}
