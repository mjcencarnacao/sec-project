package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.TokenExchangeSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.HashMap;

import static com.sec.project.configuration.StaticNodeConfiguration.LEADER_PORT;
import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.utils.Constants.MINIMUM_TRANSACTION_FEE;

@Service
public class TokenExchangeSystemServiceImplementation implements TokenExchangeSystemService {

    public HashMap<PublicKey, Integer> accountRecord = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(TokenExchangeSystemServiceImplementation.class);

    @Override
    public int check_balance(PublicKey account) {
        return accountRecord.get(account);
    }

    @Override
    public void createAccount(int publicKey) {
        accountRecord.put(getPublicKeysFromFile(true).get(publicKey), 100);
        logger.info("Created Account for: " + publicKey);
    }

    @Override
    public void transfer(PublicKey source, PublicKey destination, int amount) {
        PublicKey leaderKey = getPublicKeysFromFile(true).get(LEADER_PORT);
        if(!accountRecord.containsKey(leaderKey)) createAccount(LEADER_PORT);
        if (accountRecord.get(source) - amount >= 0) {
            accountRecord.put(source, accountRecord.get(source) - amount);
            accountRecord.put(destination, accountRecord.get(destination) + amount);
            accountRecord.put(source, accountRecord.get(source) - MINIMUM_TRANSACTION_FEE);
            accountRecord.put(leaderKey, accountRecord.get(leaderKey) + MINIMUM_TRANSACTION_FEE);
            logger.info("Transfer performed with value: " + amount);
        }
    }

}
