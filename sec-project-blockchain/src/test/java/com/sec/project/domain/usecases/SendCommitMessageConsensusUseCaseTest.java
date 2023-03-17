package com.sec.project.domain.usecases;

import com.sec.project.domain.models.enums.SendingMethod;
import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.usecases.consensus.SendCommitMessageConsensusUseCase;
import com.sec.project.utils.NetworkUtils;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.sec.project.domain.models.enums.MessageType.COMMIT;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SendCommitMessageConsensusUseCaseTest {

    @Mock
    private NetworkUtils<Message> networkUtils;

    @InjectMocks
    private SendCommitMessageConsensusUseCase sendCommitMessageUseCase;

    @Before("")
    public void beforeTestClass() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        Message message = new Message(COMMIT, -1, -1, "TEST_VALUE");
        sendCommitMessageUseCase.execute(message);
        verify(networkUtils, times(1)).sendMessage(message, SendingMethod.BROADCAST, Optional.empty(), false);
    }

}