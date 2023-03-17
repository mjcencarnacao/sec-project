package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.KeyExchangeService;
import com.sec.project.domain.usecases.exchange.ExchangeUseCaseCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.HashMap;

@Service
public class KeyExchangeServiceImplementation implements KeyExchangeService {

    /**
     * Use Cases relevant to the exchange of keys.
     */
    private final ExchangeUseCaseCollection exchangeUseCaseCollection;

    /**
     * HashMap to store the mapping of the Node ports and Public Keys.
     */
    public static final HashMap<Integer, PublicKey> publicKeyPeerHashMap = new HashMap<>();

    /**
     * Boolean indicating if an exchange was already performed or not.
     */
    public boolean exchangedPerformed = false;

    @Autowired
    public KeyExchangeServiceImplementation(ExchangeUseCaseCollection exchangeUseCaseCollection) {
        this.exchangeUseCaseCollection = exchangeUseCaseCollection;
    }

    /**
     * Execute the exchange of both types of keys (Public and Secret).
     */
    @Override
    public void exchangeKeys() {
        if (!exchangedPerformed) {
            sharePublicKey();
            shareSessionKey();
            exchangedPerformed = true;
        }
    }

    /**
     * Public Key exchange use case execution.
     */
    @Override
    public void sharePublicKey() {
        exchangeUseCaseCollection.sharePublicKeyUseCase().execute();
    }

    /**
     * Secret Key exchange use case execution.
     */
    @Override
    public void shareSessionKey() {
        exchangeUseCaseCollection.shareSecretKeyUseCase().execute();
    }

}
