package com.sec.project.domain.usecases;

import com.sec.project.domain.models.records.Message;
import com.sec.project.utils.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

public interface UseCase {
    @Autowired
    NetworkUtils<Message> utils = null;
    void execute(Message message) throws ExecutionException, InterruptedException;
}
