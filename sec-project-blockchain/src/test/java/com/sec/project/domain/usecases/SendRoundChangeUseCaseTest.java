package com.sec.project.domain.usecases;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Optional;

import static com.sec.project.domain.models.enums.MessageType.PRE_PREPARE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SendRoundChangeUseCaseTest {

    @Mock
    private NetworkUtils<Message> networkUtils;

    @InjectMocks
    private SendRoundChangeUseCase sendRoundChangeUseCase;

    @BeforeTestClass
    public void beforeTestClass() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        Message message = new Message(PRE_PREPARE, -1, -1, "TEST_VALUE");
        sendRoundChangeUseCase.execute(message);
        verify(networkUtils, times(1)).sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
    }

}