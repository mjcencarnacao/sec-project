package com.sec.project.domain.usecases;

import com.sec.project.domain.models.records.Message;

import java.util.concurrent.ExecutionException;

public interface UseCase {
    void execute(Message message) throws ExecutionException, InterruptedException;
}
