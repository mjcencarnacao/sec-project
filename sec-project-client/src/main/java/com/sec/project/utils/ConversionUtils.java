package com.sec.project.utils;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

@Component
public class ConversionUtils<T> {

    public byte[] convertObjectToBytes(@NotNull T object) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutput output = new ObjectOutputStream(stream)) {
            output.writeObject(object);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
