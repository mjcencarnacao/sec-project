package com.sec.project.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Component that holds the static configuration allowed in the project.
 */
@Component
@ConfigurationProperties("app.nodes")
public class StaticNodeConfiguration {
    /**
     * Static Ports for different nodes.
     */
    public static final List<Integer> ports = Arrays.asList(4000, 4001, 4002, 4003);

    /**
     * Faulty Nodes. Used to get the Quorum size for the given IBFT instance.
     */
    public static final int faultyNodes = 1;

    /**
     * Method that calculates the Quorum size for a given number of elements in the Blockchain.
     *
     * @return minimum messages for the quorum to take place.
     */
    public static int getQuorum() {
        return 2 * faultyNodes + 1;
    }
}
