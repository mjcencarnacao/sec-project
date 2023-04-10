package com.sec.project.domain.usecases.tes;

import com.sec.project.domain.repositories.TokenExchangeSystemService;
import com.sec.project.domain.usecases.UseCase;
import com.sec.project.models.enums.SendingMethod;
import com.sec.project.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Optional;

import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.infrastructure.repositories.ConsensusServiceImplementation.blockchainTransactions;
import static com.sec.project.infrastructure.repositories.ConsensusServiceImplementation.clientPort;
import static com.sec.project.models.enums.MessageType.CHECK_BALANCE;

@Service
public class CheckBalanceUseCase implements UseCase {

    private final NetworkUtils<Message> networkUtils;
    private final TokenExchangeSystemService tokenExchangeSystemService;

    @Autowired
    public CheckBalanceUseCase(NetworkUtils<Message> networkUtils, TokenExchangeSystemService tokenExchangeSystemService) {
        this.networkUtils = networkUtils;
        this.tokenExchangeSystemService = tokenExchangeSystemService;
    }

    @Override
    public void execute(Message message) {
        PublicKey source = getPublicKeysFromFile(true).get(message.source());
        networkUtils.sendMessage(new Message(CHECK_BALANCE, 0, 0, String.valueOf(tokenExchangeSystemService.check_balance(source)), 0, 0), SendingMethod.UNICAST, Optional.of(clientPort));
        blockchainTransactions.clientRequests().remove(0);
    }

}
