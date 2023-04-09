package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.TokenExchangeSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.HashMap;

import static com.sec.project.infrastructure.configuration.StaticNodeConfiguration.getPublicKeysOfClientFromFile;

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
        accountRecord.put(getPublicKeysOfClientFromFile().get(publicKey), 100);
        logger.info("Created Account for: " + publicKey);
    }

    @Override
    public void transfer(PublicKey source, PublicKey destination, int amount) {
        accountRecord.put(source, accountRecord.get(source) - amount);
        accountRecord.put(destination, accountRecord.get(destination) + amount);
        logger.info("Transfer performed with value: " + amount);
    }

}
