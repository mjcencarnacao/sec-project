package com.sec.project.infrastructure.repositories;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.repositories.TransactionRepository;
import com.sec.project.infrastructure.configuration.FileSystemConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.sec.project.utils.Constants.TRANSACTION_LOG_FILENAME;

public class TransactionRepositoryImplementation implements TransactionRepository {

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(TRANSACTION_LOG_FILENAME));
            lines.forEach(line -> messages.add(new Message(Long.parseLong(line.split(" ")[1]), line.split(" ")[3])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public void save(Message message) {
        try {
            FileSystemConfiguration.saveToFile(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
