package com.sec.project.domain.repositories;

import com.sec.project.domain.models.Message;

import java.util.List;

public interface TransactionRepository {
    List<Message> findAll();
    void save(Message message);
}
