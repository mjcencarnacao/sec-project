package com.sec.project.infrastructure.aspects;

import com.sec.project.domain.models.records.Message;
import com.sec.project.domain.models.valueobjects.Node;
import com.sec.project.infrastructure.annotations.Byzantine;
import com.sec.project.interfaces.CommandLineInterface;
import com.sec.project.utils.NetworkUtils;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.SocketException;
import java.net.UnknownHostException;

import static com.sec.project.domain.models.enums.Mode.REGULAR;
import static com.sec.project.domain.models.enums.Role.LEADER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ByzantineAspectTest {

    @Mock
    private NetworkUtils<Message> networkUtils;

    @Before("")
    public void beforeTestClass() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Byzantine
    public void testByzantineAspect() throws SocketException, UnknownHostException {
        CommandLineInterface.self = new Node(5000, LEADER, REGULAR);
        verify(networkUtils, times(1));
    }

}