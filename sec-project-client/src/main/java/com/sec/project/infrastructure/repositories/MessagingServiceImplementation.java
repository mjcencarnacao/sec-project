package com.sec.project.infrastructure.repositories;

import com.google.gson.Gson;
import com.sec.project.configuration.SecurityConfiguration;
import com.sec.project.domain.repositories.MessagingService;
import com.sec.project.models.enums.ReadType;
import com.sec.project.models.records.Message;
import com.sec.project.models.records.Snapshot;
import com.sec.project.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static com.sec.project.configuration.StaticNodeConfiguration.getPublicKeysFromFile;
import static com.sec.project.configuration.StaticNodeConfiguration.getQuorum;
import static com.sec.project.utils.NetworkUtils.lastReceived;

/**
 * MessagingServiceImplementation that follows the defined MessagingService contract.
 *
 * @see MessagingService
 */
@Service
public class MessagingServiceImplementation implements MessagingService {

    private final NetworkUtils<Message> networkUtils;
    private final SecurityConfiguration securityConfiguration;
    private final Logger logger = LoggerFactory.getLogger(MessagingServiceImplementation.class);

    @Autowired
    public MessagingServiceImplementation(NetworkUtils<Message> networkUtils, SecurityConfiguration securityConfiguration) {
        this.networkUtils = networkUtils;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Method that calls the generic NetworkUtils and handles delivery, to the blockchain service, of any type provided.
     *
     * @param message Message to be sent by the client service.
     */
    @Override
    public void sendMessage(Message message, ReadType readType) {
        networkUtils.sendMessage(message, readType);
        logger.info("Client sent message with type: " + message.type());
    }

    /**
     * Method that calls the generic NetworkUtils and handles responses from other processes.
     */
    @Override
    public void receiveResponse(boolean unicast) {
        if (unicast) {
            Message message = networkUtils.receiveResponse(Message.class);
            Snapshot snapshot = new Gson().fromJson(message.value(), Snapshot.class);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            snapshot.signatures().forEach(signature -> getPublicKeysFromFile(false).forEach((k, v) -> {
                if (securityConfiguration.verifySignature(v, lastReceived, signature))
                    atomicInteger.incrementAndGet();
            }));
            if (atomicInteger.get() >= getQuorum())
                logger.info("Received Stale Response from the Blockchain. Operation Successful. Value: " + snapshot.accounts().get(message.source()) + "OR: " + snapshot.accounts().get(message.destination()));
        } else
            logger.info("Received Response from the Blockchain. Operation Successful. Value: " + networkUtils.receiveQuorumResponse(Message.class).value());
    }

}
