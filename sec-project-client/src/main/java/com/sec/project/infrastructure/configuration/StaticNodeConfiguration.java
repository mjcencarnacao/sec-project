package com.sec.project.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties("app.nodes")
public class StaticNodeConfiguration {

    /**
     * Static IP address for all nodes.
     */
    private final String address = "localhost";

    /**
     * Static Ports for different nodes.
     */
    public final List<Integer> ports = Arrays.asList(4000, 4001, 4002, 4003);

}
