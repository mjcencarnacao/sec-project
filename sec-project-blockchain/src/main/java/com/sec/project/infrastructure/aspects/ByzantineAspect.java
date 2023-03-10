package com.sec.project.infrastructure.aspects;

import com.google.gson.Gson;
import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.infrastructure.annotations.Byzantine;
import com.sec.project.utils.NetworkUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.sec.project.interfaces.CommandLineInterface.self;

@Aspect
@Component
public class ByzantineAspect {

    private final Gson gson;
    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(ByzantineAspect.class);

    @Autowired
    public ByzantineAspect(Gson gson, NetworkUtils<Message> networkUtils) {
        this.gson = gson;
        this.networkUtils = networkUtils;
    }

    @Around("@annotation(com.sec.project.infrastructure.annotations.Byzantine) && args(message)")
    public Object execute(ProceedingJoinPoint join, Message message) throws Throwable {
        if (self.getMode().isByzantine()) {
            networkUtils.sendMessage(self.craftByzantineMessage(message), SendingMethod.BROADCAST, Optional.empty());
            logger.info("Node sent Byzantine message.");
            return null;
        }
        return join.proceed();
    }

}
