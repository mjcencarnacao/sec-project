package com.sec.project.infrastructure.aspects;

import com.sec.project.domain.models.records.Message;
import com.sec.project.infrastructure.annotations.FlushUDPBuffer;
import com.sec.project.utils.NetworkUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sec.project.interfaces.CommandLineInterface.self;
import static com.sec.project.utils.Constants.DEFAULT_TIMEOUT;

@Aspect
@Component
public class FlushUDPBufferAspect {

    private final NetworkUtils<Message> networkUtils;
    private final Logger logger = LoggerFactory.getLogger(FlushUDPBufferAspect.class);

    @Autowired
    public FlushUDPBufferAspect(NetworkUtils<Message> networkUtils) {
        this.networkUtils = networkUtils;
    }

    /**
     * Method that cleans the UDP buffer from incoming messages (useful when starting a new consensus round, to ensure no
     * other network message gets on the way for the IBFT instance).
     *
     * @param join the rest of the method that is annotated.
     */
    @Around(value = "@annotation(flushUDPBuffer)")
    public Object execute(ProceedingJoinPoint join, FlushUDPBuffer flushUDPBuffer) throws Throwable {
        if (self.getRole().isLeader() || flushUDPBuffer.override())
            try {
                while (self.getConnection().datagramSocket().getReceiveBufferSize() != 0) {
                    self.getConnection().datagramSocket().setSoTimeout(DEFAULT_TIMEOUT);
                    networkUtils.receiveResponse(true);
                }
            } catch (Exception exception) {
                self.getConnection().datagramSocket().setSoTimeout(0);
                logger.info("Cleaned UDP buffer with success.");
            }
        return join.proceed();
    }
}
