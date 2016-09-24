package ru.spbau.mit.vcs;

import com.beust.jcommander.JCommander;
import ru.spbau.mit.vcs.commands.CommandFactory;
import ru.spbau.mit.vcs.commands.VCSCommand;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.context.VCSContextSerializer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class VCS {

    public static final String FOLDER = ".vcs";
    private static final String ENV_PATH = FOLDER + "/env.json";

    private VCSContext context;

    public void run() throws IOException {
        initDirectory();
        CommandFactory.initCommander().usage();
        context = VCSContextSerializer.readContext(ENV_PATH);
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    executeCommand(line);
                } catch (Exception e) {
                    System.out.println("Failed to execute command: " + e.getMessage());
                    e.printStackTrace();
                }
                VCSContextSerializer.saveContext(context, ENV_PATH);
            }
        }
        VCSContextSerializer.saveContext(context, ENV_PATH);
    }

    private boolean initDirectory() {
        return new File(FOLDER).mkdirs();
    }

    private void executeCommand(String line) {
        JCommander commander = CommandFactory.initCommander();
        commander.parse(line.split(" "));
        JCommander parsed = commander.getCommands().get(commander.getParsedCommand());
        VCSCommand command = (VCSCommand) parsed.getObjects().get(0);
        try {
            command.execute(context);
        } catch (ru.spbau.mit.vcs.exception.VCSException e) {
            e.printStackTrace();
        }
    }
}
