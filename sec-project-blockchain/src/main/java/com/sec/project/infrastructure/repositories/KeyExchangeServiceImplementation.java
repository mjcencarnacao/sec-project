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

    @Autowired
    public KeyExchangeServiceImplementation(ExchangeUseCaseCollection exchangeUseCaseCollection) {
        this.exchangeUseCaseCollection = exchangeUseCaseCollection;
    }

    /**
     * Execute the exchange of both types of keys. Callback is triggered after exchanged is successful.
     *
     * @param callback IBFT algorithm called after exchange.
     */
    @Override
    public void exchangeKeys(Runnable callback) {
        sharePublicKey();
        shareSessionKey();
        callback.run();
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
