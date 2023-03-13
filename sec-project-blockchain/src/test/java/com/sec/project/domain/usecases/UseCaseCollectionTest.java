package com.sec.project.domain.usecases;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UseCaseCollectionTest {

    @Autowired
    private UseCaseCollection useCaseCollection;

    @Test
    public void testUseCaseCollection() {
        assertNotNull(useCaseCollection.sendRoundChangeUseCase());
        assertNotNull(useCaseCollection.sendPrepareMessageUseCase());
        assertNotNull(useCaseCollection.sendCommitMessageUseCase());
        assertNotNull(useCaseCollection.sendPrePrepareMessageUseCase());
    }

}