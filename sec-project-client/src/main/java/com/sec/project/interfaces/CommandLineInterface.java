package com.sec.project.interfaces;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.usecases.SendMessageUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.logging.Logger;

/**
 * Spring Shell Component that allows a more interactive way of the user to communicate, providing custom commands defined
 * bellow.
 */
@ShellComponent
public class CommandLineInterface {

    private final SendMessageUseCase sendMessageUseCase;
    private final Logger logger = Logger.getLogger(CommandLineInterface.class.getName());

    @Autowired
    public CommandLineInterface(SendMessageUseCase sendMessageUseCase) {
        this.sendMessageUseCase = sendMessageUseCase;
    }

    /**
     * Console method that allows a client to send a request to append a message to the blockchain.
     * This command can be issued like the following example: append -s <MESSAGE>
     *
     * @param message Message written in the console, that is followed by the -s command, to be sent to the blockchain service.
     */
    @ShellMethod("Append a message to the blockchain.")
    public void append(@ShellOption(value = "-s", defaultValue = "") String message) {
        logger.info(String.format("Client sent message with value %s", message));
        sendMessageUseCase.execute(new Message(message));

    }

    /**
     * Logs out of the shell application and closes the client process.
     */
    @ShellMethod("Exit the application.")
    public void logout() {
        logger.info("Exit command issued.");
        System.exit(0);
    }

}
