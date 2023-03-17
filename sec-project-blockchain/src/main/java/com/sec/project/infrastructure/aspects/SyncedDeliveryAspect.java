package com.sec.project.infrastructure.aspects;

import com.sec.project.infrastructure.annotations.SyncedDelivery;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SyncedDeliveryAspect {

    /**
     * Method that cleans the UDP buffer from incoming messages (useful when starting a new consensus round, to ensure no
     * other network message gets on the way for the IBFT instance).
     *
     * @param join the rest of the method that is annotated.
     */
    @Around(value = "@annotation(syncedDelivery)")
    public Object execute(ProceedingJoinPoint join, SyncedDelivery syncedDelivery) throws Throwable {
        Thread.sleep(syncedDelivery.value());
        return join.proceed();
    }
}
