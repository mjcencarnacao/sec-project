package com.sec.project.interfaces;

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

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

@ShellComponent
public class CommandLineInterface {

    public static Node self;
    private final ConsensusService consensusService;
    Logger logger = LoggerFactory.getLogger(CommandLineInterface.class);

    @Autowired
    public CommandLineInterface(ConsensusService consensusService) {
        this.consensusService = consensusService;
    }

    @ShellMethod("Create a new Node.")
    public void init(@ShellOption(value = "-p") int port, @ShellOption(value = "-r", defaultValue = "MEMBER") Role role, @ShellOption(value = "-m", defaultValue = "REGULAR") Mode mode) throws SocketException, UnknownHostException {
        self = new Node(port, role, mode);
        logger.info(String.format("Started new Node on Port: %d with role %s", self.getConnection().datagramSocket().getLocalPort(), self.getRole().name()));
    }

    @ShellMethod("Start the Consensus service.")
    public void start() throws ExecutionException, InterruptedException {
        consensusService.start();
        logger.info("Started IBFT protocol.");
    }

    @ShellMethod("Exit the application.")
    public void logout() {
        logger.info("Exit command issued.");
        System.exit(0);
    }

}
