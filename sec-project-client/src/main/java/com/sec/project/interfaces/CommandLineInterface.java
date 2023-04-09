package com.sec.project.interfaces;

import com.sec.project.domain.models.Message;
import com.sec.project.domain.usecases.ReceiveResponseUseCase;
import com.sec.project.domain.usecases.SendMessageUseCase;
import com.sec.project.infrastructure.configuration.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Optional;
import java.util.logging.Logger;

import static com.sec.project.domain.enums.MessageType.*;
import static com.sec.project.utils.NetworkUtils.connection;

/**
 * Spring Shell Component that allows a more interactive way of the user to communicate, providing custom commands defined
 * bellow.
 */
@ShellComponent
public class CommandLineInterface {

    private final SendMessageUseCase sendMessageUseCase;
    private final SecurityConfiguration securityConfiguration;
    private final ReceiveResponseUseCase receiveResponseUseCase;
    private final Logger logger = Logger.getLogger(CommandLineInterface.class.getName());

    @Autowired
    public CommandLineInterface(SendMessageUseCase sendMessageUseCase, ReceiveResponseUseCase receiveResponseUseCase, SecurityConfiguration securityConfiguration) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.securityConfiguration = securityConfiguration;
        this.receiveResponseUseCase = receiveResponseUseCase;
    }

    /**
     * Console method that allows a client to send a request to append a message to the blockchain.
     * This command can be issued like the following example: append -s <MESSAGE>
     *
     * @param message Message written in the console, that is followed by the -s command, to be sent to the blockchain service.
     */
    @ShellMethod("Append a message to the blockchain.")
    public void append(@ShellOption(value = "-s", defaultValue = "") String message) {
        sendMessageUseCase.execute(new Message(null, message, connection.datagramSocket().getLocalPort(), 0));
        receiveResponseUseCase.execute();
    }

    @ShellMethod(key = "create_account", value = "Create a Client Account.")
    public void createAccount() {
        securityConfiguration.writePublicKeyToFile();
        sendMessageUseCase.execute(new Message(CREATE_ACCOUNT, "ok", connection.datagramSocket().getLocalPort(), 0));
    }

    @ShellMethod("Transfer to an Account.")
    public void transfer(int id, int amount) {
        sendMessageUseCase.execute(new Message(TRANSFER, String.valueOf(amount), connection.datagramSocket().getLocalPort(), id));
    }

    @ShellMethod(key = "check_balance", value = "Check balance of an Account.")
    public void checkBalance(int port) {
        sendMessageUseCase.execute(new Message(CHECK_BALANCE, "", port, 0));
        receiveResponseUseCase.execute();
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
