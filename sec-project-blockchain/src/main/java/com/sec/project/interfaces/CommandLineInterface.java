package com.sec.project.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class CommandLineInterface {

    Logger logger = LoggerFactory.getLogger(CommandLineInterface.class);

    @ShellMethod("Exit the application.")
    public void logout() {
        logger.info("Exit command issued.");
        System.exit(0);
    }

}
