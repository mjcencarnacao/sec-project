package com.sec.project.interfaces;

import com.sec.project.configuration.SecurityConfiguration;
import com.sec.project.domain.usecases.UseCaseCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

import static com.sec.project.utils.Constants.CLIENT_KEY_STORAGE;
import static com.sec.project.utils.NetworkUtils.connection;

/**
 * Spring Shell Component that allows a more interactive way of the user to communicate, providing custom commands defined
 * bellow.
 */
@ShellComponent
public class CommandLineInterface {

    private final UseCaseCollection useCaseCollection;
    private final SecurityConfiguration securityConfiguration;
    private final Logger logger = Logger.getLogger(CommandLineInterface.class.getName());

    @Autowired
    public CommandLineInterface(UseCaseCollection useCaseCollection, SecurityConfiguration securityConfiguration) {
        this.useCaseCollection = useCaseCollection;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Console method that allows a client to register in the TES system.
     * This command can be issued like the following example: create_account
     */
    @ShellMethod(key = "create_account", value = "Create a Client Account.")
    public void createAccount() {
        securityConfiguration.writePublicKeyToFile(connection.datagramSocket().getLocalPort(), true);
        useCaseCollection.createAccountUseCase().execute();
    }

    /**
     * Console method that allows a client to send a request to transfer a given amount.
     * This command can be issued like the following example: transfer -d <DESTINATION> -a <AMOUNT>
     *
     * @param identifier port of the destination address, in order to retrieve the public key stored in a static way.
     * @param amount     to transfer for a given operation.
     */
    @ShellMethod("Transfer to an Account.")
    public void transfer(int identifier, int amount) {
        useCaseCollection.transferUseCase().execute(identifier, amount);
    }

    /**
     * Console method that allows a client to check the balance for a given account.
     * This command can be issued like the following example: check_balance -d <DESTINATION>
     *
     * @param port for the account to check the balance.
     */
    @ShellMethod(key = "check_balance", value = "Check balance of an Account.")
    public void checkBalance(int port) {
        useCaseCollection.checkBalanceUseCase().execute(port);
    }

    /**
     * Logs out of the shell application and closes the client process.
     */
    @ShellMethod("Exit the application.")
    public void logout() {
        logger.info("Exit command issued.");
        Arrays.stream(Objects.requireNonNull(new File(CLIENT_KEY_STORAGE).listFiles())).toList().forEach(File::delete);
        System.exit(0);
    }

}
