package com.sec.project.interfaces;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.usecases.SendMessageUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.logging.Logger;

@ShellComponent
public class CommandLineInterface {

    @Autowired
    private SendMessageUseCase sendMessageUseCase;

    Logger logger = Logger.getLogger(CommandLineInterface.class.getName());

    @ShellMethod("Append a message to the blockchain.")
    public void append(@ShellOption(value = "-s", defaultValue = "") String message) throws IOException {
        logger.info(String.format("Client sent message with value %s", message));
        sendMessageUseCase.execute(new Message(message));
    }

    @ShellMethod("Exit the application.")
    public void logout() {
        logger.info("Exit command issued.");
        System.exit(0);
    }

}
