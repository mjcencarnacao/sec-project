package com.sec.project.domain.usecases.exchange;

import com.google.gson.Gson;
import com.sec.project.domain.models.records.MessageTransferObject;
import com.sec.project.infrastructure.annotations.SyncedDelivery;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.utils.NetworkUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Optional;

import static com.sec.project.domain.models.enums.SendingMethod.BROADCAST;
import static com.sec.project.interfaces.CommandLineInterface.self;
import static com.sec.project.utils.Constants.SYMMETRIC_ALGORITHM;

@Service
public class ShareSecretKeyConsensusUseCase implements ExchangeUseCase {

    /**
     * GSON to allow serialization
     */
    private final Gson gson;

    /**
     * Network Utils to perform the exchange of Secret Keys with other peers.
     */
    private final NetworkUtils<byte[]> networkUtils;

    /**
     * Security Configuration to retrieve Key logic.
     */
    private final SecurityConfiguration securityConfiguration;

    @Autowired
    public ShareSecretKeyConsensusUseCase(Gson gson, NetworkUtils<byte[]> networkUtils, SecurityConfiguration securityConfiguration) {
        this.gson = gson;
        this.networkUtils = networkUtils;
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    public void execute() {
        if (self.getRole().isLeader())
            sendEncryptedSecretKey();
        else
            receiveEncryptedSecretKey();
    }

    @SyncedDelivery
    private void sendEncryptedSecretKey() {
        securityConfiguration.setSymmetricKey(securityConfiguration.getEncodedSymmetricKey());
        byte[] encryptedKey = securityConfiguration.asymmetricEncoding(securityConfiguration.getSymmetricKey().getEncoded());
        networkUtils.sendMessage(encryptedKey, BROADCAST, Optional.empty(), true);
    }

    private void receiveEncryptedSecretKey() {
        ImmutablePair<Integer, MessageTransferObject> response = networkUtils.receiveResponse(true);
        byte[] encryptedKey = gson.fromJson(new String(response.right.data()).trim(), byte[].class);
        byte[] decryptedKey = securityConfiguration.asymmetricDecoding(encryptedKey, response.left);
        SecretKey sessionKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, SYMMETRIC_ALGORITHM);
        securityConfiguration.setSymmetricKey(sessionKey);
    }

}
