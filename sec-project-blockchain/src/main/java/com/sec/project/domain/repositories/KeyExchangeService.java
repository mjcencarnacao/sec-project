package com.sec.project.domain.repositories;

import java.util.concurrent.ExecutionException;

public interface KeyExchangeService {
    void exchangeKeys();

    void sharePublicKey() throws ExecutionException, InterruptedException;

    void shareSessionKey();

}
