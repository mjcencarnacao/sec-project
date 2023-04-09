package com.sec.project.domain.repositories;

import java.security.PublicKey;

public interface TokenExchangeSystemService {
    void createAccount(int publicKey);

    int check_balance(PublicKey account);

    void transfer(PublicKey source, PublicKey destination, int amount);
}
