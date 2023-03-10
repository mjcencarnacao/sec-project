package com.sec.project.domain.repositories;

import com.sec.project.domain.models.Message;

public interface MessagingService {

    void receiveResponse();

    void sendMessage(Message message);
}
