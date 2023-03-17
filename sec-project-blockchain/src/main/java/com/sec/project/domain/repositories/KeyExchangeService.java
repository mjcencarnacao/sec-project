package com.sec.project.domain.repositories;

import org.springframework.stereotype.Service;

/**
 * Contract for the KeyExchangeServiceImplementation where the both asymmetric and symmetric key exchange will take place.
 *
 * @see com.sec.project.infrastructure.repositories.KeyExchangeServiceImplementation
 */
@Service
public interface KeyExchangeService {
    void exchangeKeys();

    void sharePublicKey();

    void shareSessionKey();
}
