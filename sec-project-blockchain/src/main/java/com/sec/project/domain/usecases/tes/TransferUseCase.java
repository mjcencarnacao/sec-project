package com.sec.project.domain.usecases.tes;

import com.sec.project.domain.repositories.TokenExchangeSystemService;
import com.sec.project.domain.usecases.UseCase;
import com.sec.project.models.records.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.infrastructure.repositories.ConsensusServiceImplementation.blockchainTransactions;

@Service
public class TransferUseCase implements UseCase {

    private final TokenExchangeSystemService tokenExchangeSystemService;

    @Autowired
    public TransferUseCase(TokenExchangeSystemService tokenExchangeSystemService) {
        this.tokenExchangeSystemService = tokenExchangeSystemService;
    }

    @Override
    public void execute(Message message) {
        blockchainTransactions.queue().add(message);
        PublicKey source = getPublicKeysFromFile(true).get(message.source());
        PublicKey destination = getPublicKeysFromFile(true).get(message.destination());
        tokenExchangeSystemService.transfer(source, destination, Integer.parseInt(message.value()));
        blockchainTransactions.clientRequests().remove(0);
    }

}
