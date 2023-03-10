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
    public final List<Integer> ports = Arrays.asList(4000, 4001, 4002, 4003);

    /**
     * Method that calculates the Quorum size for a given number of elements in the Blockchain.
     *
     * @return minimum messages for the quorum to take place.
     */
    public final int getQuorum() {
        return 3;
    }

    /**
     * Assuming we have One faulty node (Byzantine).
     * 2 * 1 + 1 = 3
     */
    public final int getQuorum() {
        return 3;
    }

}
