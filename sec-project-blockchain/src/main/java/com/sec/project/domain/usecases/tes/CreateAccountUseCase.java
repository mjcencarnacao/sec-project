package com.sec.project.domain.usecases.tes;

import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.repositories.TokenExchangeSystemService;
import com.sec.project.domain.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sec.project.infrastructure.repositories.ConsensusServiceImplementation.blockchainTransactions;

@Service
public class CreateAccountUseCase implements UseCase {

    private final TokenExchangeSystemService tokenExchangeSystemService;

    @Autowired
    public CreateAccountUseCase(TokenExchangeSystemService tokenExchangeSystemService){
        this.tokenExchangeSystemService = tokenExchangeSystemService;
    }

    @Override
    public void execute(Message message) {
        tokenExchangeSystemService.createAccount(message.source());
        blockchainTransactions.clientRequests().remove(0);
    }

}
