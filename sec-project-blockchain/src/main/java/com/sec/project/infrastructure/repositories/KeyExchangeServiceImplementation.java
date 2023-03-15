package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.KeyExchangeService;
import com.sec.project.domain.usecases.exchange.ExchangeUseCaseCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class KeyExchangeServiceImplementation implements KeyExchangeService {

    /**
     * HashMap to store the mapping of the Node ports and Public Keys.
     */
    public static final HashMap<Integer, PublicKey> publicKeyPeerHashMap = new HashMap<>();

    /**
     * Use Cases relevant to the exchange of keys.
     */
    private final ExchangeUseCaseCollection exchangeUseCaseCollection;


    @Autowired
    public KeyExchangeServiceImplementation(ExchangeUseCaseCollection exchangeUseCaseCollection) {
        this.exchangeUseCaseCollection = exchangeUseCaseCollection;
    }

    @Override
    public void exchangeKeys() {
        sharePublicKey();
        shareSessionKey();
    }

    @Override
    public void sharePublicKey() {
        exchangeUseCaseCollection.sharePublicKeyUseCase().execute(null);
    }

    @Override
    public void shareSessionKey() {
        exchangeUseCaseCollection.shareSecretKeyUseCase().execute(null);
    }

}
