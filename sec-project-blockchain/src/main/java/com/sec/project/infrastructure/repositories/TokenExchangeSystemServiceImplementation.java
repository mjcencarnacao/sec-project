package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.repositories.TokenExchangeSystemService;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.HashMap;

import static com.sec.project.infrastructure.configuration.StaticNodeConfiguration.getPublicKeysOfClientFromFile;

@Service
public class TokenExchangeSystemServiceImplementation implements TokenExchangeSystemService {

    public HashMap<PublicKey, Integer> accountRecord = new HashMap<>();

    @Override
    public int check_balance(PublicKey account) {
        return accountRecord.get(account);
    }

    @Override
    public void createAccount(int publicKey) {
        PublicKey pk = getPublicKeysOfClientFromFile().get(publicKey);
        accountRecord.put(pk, 100);
        System.out.println("Created Account for: " + publicKey);
    }

    @Override
    public void transfer(PublicKey source, PublicKey destination, int amount) {
        accountRecord.put(source, accountRecord.get(source) - amount);
        accountRecord.put(destination, accountRecord.get(destination) + amount);
    }

}
