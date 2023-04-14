package com.sec.project.interfaces;

import com.sec.project.configuration.SecurityConfiguration;
import com.sec.project.domain.models.enums.Mode;
import com.sec.project.domain.models.enums.Role;
import com.sec.project.domain.models.valueobjects.Node;
import com.sec.project.domain.repositories.ConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import static com.sec.project.infrastructure.repositories.ConsensusServiceImplementation.blockchainTransactions;

/**
 * Spring Shell Component that allows a more interactive way of the user to communicate, providing custom commands defined
 * bellow.
 */
@ShellComponent
public class CommandLineInterface {

    public static Node self;
    public static DatagramSocket clientListener;
    private final ConsensusService consensusService;
    private final SecurityConfiguration securityConfiguration;
    private final Logger logger = LoggerFactory.getLogger(CommandLineInterface.class);

    @Autowired
    public CommandLineInterface(ConsensusService consensusService, SecurityConfiguration securityConfiguration) {
        this.consensusService = consensusService;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Shell method that allows a user to create a custom node in the Blockchain, passing its arguments.
     * An example of the command would be: init -p 4000 -r LEADER -m BYZANTINE
     *
     * @param port where the node should listen for messages.
     * @param role of the starting node. Default is set to MEMBER.
     * @param mode which can either be BYZANTINE or REGULAR. Default is REGULAR.
     * @throws SocketException      in case there is any issue encountered with the UDP socket.
     * @throws UnknownHostException in case the host cannot be determined.
     */
    @ShellMethod("Create a new Node.")
    public void init(@ShellOption(value = "-p") int port, @ShellOption(value = "-r", defaultValue = "MEMBER") Role role, @ShellOption(value = "-m", defaultValue = "REGULAR") Mode mode) throws SocketException, UnknownHostException {
        self = new Node(port, role, mode);
        clientListener = new DatagramSocket(port + 1000);
        securityConfiguration.writePublicKeyToFile(port, false);
        logger.info(String.format("Started new Node on Port: %d with role %s", self.getConnection().datagramSocket().getLocalPort(), self.getRole().name()));
    }

    /**
     * Starts listening for messages and performing the IBFT protocol.
     * This command should be issued after issuing the init shell method.
     * The method can be executed in the command line by writing: init
     */
    @ShellMethod("Start the Consensus service.")
    public void start() {
        logger.info("Started the Key Exchange and IBFT protocol.");
        consensusService.start(Optional.empty());
    }

    /**
     * Prints the Messages Stored in the Blockchain at a given moment.
     */
    @ShellMethod("Print Blockchain Values to the Console.")
    public void print() {
        blockchainTransactions.transactions().forEach(System.out::println);
    }

    /**
     * Logs out of the shell application and closes the blockchain node process.
     */
    @ShellMethod("Exit the application.")
    public void logout() {
        logger.info("Exit command issued.");
        System.exit(0);
    }

}
