package com.sec.project.domain.usecases.exchange;

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
     * Network Utils to perform the exchange of Secret Keys with other peers.
     */
    private final NetworkUtils<byte[]> networkUtils;

    /**
     * Security Configuration to retrieve Key logic.
     */
    private final SecurityConfiguration<byte[]> securityConfiguration;

    @Autowired
    public ShareSecretKeyConsensusUseCase(NetworkUtils<byte[]> networkUtils, SecurityConfiguration<byte[]> securityConfiguration) {
        this.networkUtils = networkUtils;
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    public void execute() {
        if (self.getRole().isLeader()) {
            securityConfiguration.setSymmetricKey(securityConfiguration.getEncodedSymmetricKey());
            byte[] encryptedKey = securityConfiguration.asymmetricEncoding(securityConfiguration.getSymmetricKey().getEncoded());
            networkUtils.sendMessage(encryptedKey, BROADCAST, Optional.empty(), true);
        } else {
            ImmutablePair<Integer, byte[]> response = networkUtils.receiveResponse(byte[].class, true);
            byte[] decryptedKey = securityConfiguration.asymmetricDecoding(response.right, response.left);
            SecretKey sessionKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, SYMMETRIC_ALGORITHM);
            securityConfiguration.setSymmetricKey(sessionKey);
        }
    }

}
