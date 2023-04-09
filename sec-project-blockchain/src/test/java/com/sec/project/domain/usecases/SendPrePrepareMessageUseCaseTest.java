package com.sec.project.domain.usecases;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.models.valueobjects.Node;
import com.sec.project.domain.usecases.consensus.SendPrePrepareMessageUseCase;
import com.sec.project.interfaces.CommandLineInterface;
import com.sec.project.utils.NetworkUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import static com.sec.project.domain.models.enums.MessageType.PRE_PREPARE;
import static com.sec.project.domain.models.enums.Mode.REGULAR;
import static com.sec.project.domain.models.enums.Role.LEADER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SendPrePrepareMessageUseCaseTest {

    @Mock
    private NetworkUtils<Message> networkUtils;

    @InjectMocks
    private SendPrePrepareMessageUseCase sendPrePrepareMessageUseCase;

    @BeforeTestClass
    public void beforeTestClass() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() throws SocketException, UnknownHostException {
        Message message = new Message(PRE_PREPARE, -1, -1, "TEST_VALUE");
        CommandLineInterface.self = new Node(5000, LEADER, REGULAR);
        sendPrePrepareMessageUseCase.execute(message);
        verify(networkUtils, times(1)).sendMessage(message, SendingMethod.BROADCAST, Optional.empty());
    }
}