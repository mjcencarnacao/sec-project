package com.sec.project.domain.repositories;

import com.sec.project.models.records.Snapshot;

import java.security.PublicKey;
import java.util.List;

public interface TokenExchangeSystemService {
    Snapshot getCurrentSnapshot();

    void createAccount(int publicKey);

    int check_balance(PublicKey account);

    void createSnapshot(List<byte[]> signatures);

    void transfer(PublicKey source, PublicKey destination, int amount);
}
