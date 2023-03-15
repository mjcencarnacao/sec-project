package com.sec.project.domain.usecases.exchange;

import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.usecases.UseCase;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import com.sec.project.infrastructure.configuration.StaticNodeConfiguration;
import com.sec.project.utils.NetworkUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sec.project.domain.models.enums.SendingMethod.BROADCAST;
import static com.sec.project.infrastructure.repositories.KeyExchangeServiceImplementation.publicKeyPeerHashMap;
import static com.sec.project.interfaces.CommandLineInterface.self;

@Service
public class SharePublicKeyUseCase implements UseCase {

    /**
     * Network Utils to perform the exchange of Public Keys with other peers.
     */
    private final NetworkUtils<byte[]> networkUtils;

    /**
     * Security Configuration to retrieve Key logic.
     */
    private final SecurityConfiguration<byte[]> securityConfiguration;

    @Autowired
    public SharePublicKeyUseCase(NetworkUtils<byte[]> networkUtils, SecurityConfiguration<byte[]> securityConfiguration) {
        this.networkUtils = networkUtils;
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    public void execute(Message message) {
        networkUtils.sendMessage(securityConfiguration.getPublicKey().getEncoded(), BROADCAST, Optional.empty(), true);
        receivePublicKeysFromPeers();
    }

    private void receivePublicKeysFromPeers() {
        try {
            while (StaticNodeConfiguration.ports.size() != publicKeyPeerHashMap.size()) {
                self.getConnection().datagramSocket().setSoTimeout(5000);
                ImmutablePair<Integer, byte[]> response = networkUtils.receiveResponse(byte[].class, true);
                publicKeyPeerHashMap.put(response.left, securityConfiguration.bytesArrayToPublicKey(response.right));
            }
            self.getConnection().datagramSocket().setSoTimeout(0);
        } catch (Exception e) {
            execute(null);
        }
    }
}
