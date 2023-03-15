package com.sec.project.domain.usecases;

import com.sec.project.domain.usecases.consensus.ConsensusUseCaseCollection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UseCaseCollectionTest {

    @Autowired
    private ConsensusUseCaseCollection useCaseCollection;

    @Test
    public void testUseCaseCollection() {
        assertNotNull(useCaseCollection.sendRoundChangeUseCase());
        assertNotNull(useCaseCollection.sendPrepareMessageUseCase());
        assertNotNull(useCaseCollection.sendCommitMessageUseCase());
        assertNotNull(useCaseCollection.sendPrePrepareMessageUseCase());
    }

}