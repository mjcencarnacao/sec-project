package com.sec.project.infrastructure.configuration;

import com.sec.project.domain.models.Message;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static com.sec.project.utils.Constants.TRANSACTION_LOG_FILENAME;

public class FileSystemConfiguration {

    public static void saveToFile(Message message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_LOG_FILENAME, true));
        writer.write(String.format("timestamp: %s message: %s\n", message.timestamp(), message.value()));
        writer.close();
    }

}
