package com.sec.project.domain.usecases;

import com.sec.project.domain.models.records.Message;

import java.util.concurrent.ExecutionException;

/**
 * Contract for the several use cases needed to support the projects functionalities.
 *
 * @see SendRoundChangeUseCase
 * @see SendCommitMessageUseCase
 * @see SendPrepareMessageUseCase
 * @see SendPrePrepareMessageUseCase
 */
public interface UseCase {
    void execute(Message message) throws ExecutionException, InterruptedException;
}
