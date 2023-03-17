package com.sec.project.domain.usecases.exchange;

import com.google.gson.Gson;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.infrastructure.annotations.SyncedDelivery;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import com.sec.project.infrastructure.repositories.ConsensusServiceImplementation;
import com.sec.project.utils.NetworkUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.sec.project.domain.models.enums.SendingMethod.BROADCAST;
import static com.sec.project.infrastructure.repositories.KeyExchangeServiceImplementation.publicKeyPeerHashMap;

@Service
public class SharePublicKeyConsensusUseCase implements ExchangeUseCase {

    /**
     * Network Utils to perform the exchange of Public Keys with other peers.
     */
    private final NetworkUtils<byte[]> networkUtils;

    /**
     * Security Configuration to retrieve Key logic.
     */
    private final SecurityConfiguration securityConfiguration;

    private final Logger logger = LoggerFactory.getLogger(ConsensusServiceImplementation.class);

    @Autowired
    public SharePublicKeyConsensusUseCase(NetworkUtils<byte[]> networkUtils, SecurityConfiguration securityConfiguration) {
        this.networkUtils = networkUtils;
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    public void execute() {
        CompletableFuture<Void> sendKeyFuture = CompletableFuture.runAsync(this::sendPublicKey);
        CompletableFuture<Void> receiveKeysFuture = CompletableFuture.runAsync(this::receivePublicKeysFromPeers);
        CompletableFuture.allOf(receiveKeysFuture, sendKeyFuture).join();
    }

    @Async
    @SyncedDelivery
    public void sendPublicKey() {
        networkUtils.sendMessage(securityConfiguration.getPublicKey().getEncoded(), BROADCAST, Optional.empty(), true);
    }

    @Async
    public void receivePublicKeysFromPeers() {
        try {
            Gson gson = new Gson();
            while (StaticNodeConfiguration.ports.size() != publicKeyPeerHashMap.size()) {
                ImmutablePair<Integer, MessageTransferObject> response = networkUtils.receiveResponse(true);
                byte[] key = gson.fromJson(new String(response.right.data()).trim(), byte[].class);
                publicKeyPeerHashMap.put(response.left, securityConfiguration.bytesArrayToPublicKey(key));
            }
        } catch (Exception e) {
            logger.error("Error retrieving Public Keys from Peers.");
        }
        publicKeyPeerHashMap.forEach((k, v) -> System.out.println("Got Public Key from Node: " + k));
    }
}
