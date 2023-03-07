package com.sec.project.utils;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

@Component
public class ConversionUtils<T> {

    Logger logger = LoggerFactory.getLogger(ConversionUtils.class);

    public byte[] convertObjectToBytes(@NotNull T object) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutput output = new ObjectOutputStream(stream)) {
            output.writeObject(object);
            return stream.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
