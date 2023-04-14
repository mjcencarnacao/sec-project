package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.TokenExchangeSystemService;
import com.sec.project.models.records.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.sec.project.configuration.StaticNodeConfiguration.*;
import static com.sec.project.utils.Constants.MINIMUM_TRANSACTION_FEE;

@Service
public class TokenExchangeSystemServiceImplementation implements TokenExchangeSystemService {

    public HashMap<PublicKey, Integer> accountRecord = new HashMap<>();
    public Snapshot currentSnapshot = new Snapshot(new HashMap<>(), new LinkedList<>());
    private final Logger logger = LoggerFactory.getLogger(TokenExchangeSystemServiceImplementation.class);

    @Override
    public int check_balance(PublicKey account) {
        return accountRecord.get(account);
    }

    @Override
    public void createSnapshot(List<byte[]> signatures) {
        HashMap<Integer, Integer> snapshot = new HashMap<>();
        getAllPublicKeysIntegersFromFile().forEach((integer, publicKey) -> {
                    if (accountRecord.containsKey(publicKey))
                        snapshot.put(integer, accountRecord.get(publicKey));
                }
        );
        this.currentSnapshot = new Snapshot(snapshot, signatures);
    }

    @Override
    public Snapshot getCurrentSnapshot() {
        return this.currentSnapshot;
    }

    @Override
    public void createAccount(int publicKey) {
        accountRecord.put(getPublicKeysFromFile(true).get(publicKey), 100);
        logger.info("Created Account for: " + publicKey);
    }

    @Override
    public void transfer(PublicKey source, PublicKey destination, int amount) {
        PublicKey leaderKey = getPublicKeysFromFile(true).get(LEADER_PORT);
        if (!accountRecord.containsKey(leaderKey)) createAccount(LEADER_PORT);
        exchangeBetweenAccounts(source, destination, amount);
        exchangeBetweenAccounts(source, leaderKey, MINIMUM_TRANSACTION_FEE);
        logger.info("Transfer performed with value: " + amount);
    }

    private void exchangeBetweenAccounts(PublicKey source, PublicKey destination, int amount) {
        if (accountRecord.get(source) - amount >= 0) {
            accountRecord.put(source, accountRecord.get(source) - amount);
            accountRecord.put(destination, accountRecord.get(destination) + amount);
        }
    }
}
