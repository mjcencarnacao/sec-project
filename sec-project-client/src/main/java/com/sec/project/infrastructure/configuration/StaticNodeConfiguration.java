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
     * Static Port List for different nodes available on the blockchain service.
     */
    public final List<Integer> ports = Arrays.asList(4000, 4001, 4002, 4003);
}
