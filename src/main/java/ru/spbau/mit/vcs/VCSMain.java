package ru.spbau.mit.vcs;

import com.beust.jcommander.JCommander;
import ru.spbau.mit.vcs.commands.CommandFactory;
import ru.spbau.mit.vcs.commands.Command;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class VCSMain {
    private static final String ENV_PATH = VCS.FOLDER + "/env.json";

    private VCS vcs;

    public void run() throws IOException {
        initDirectory();
        CommandFactory.initCommander().usage();
        vcs = new File(ENV_PATH).exists() ? VCSSerializer.readEnv(ENV_PATH) : new VCS();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    executeCommand(line);
                } catch (Exception e) {
                    System.out.println("Failed to execute command: " + e.getMessage());
                    e.printStackTrace();
                }
                VCSSerializer.saveEnv(vcs, ENV_PATH);
            }
        }
        VCSSerializer.saveEnv(vcs, ENV_PATH);
    }

    public static void main(String[] args) throws IOException {
        new VCSMain().run();
    }

    private boolean initDirectory() {
        return new File(VCS.FOLDER).mkdirs();
    }

    private void executeCommand(String line) throws VCSException, IOException {
        JCommander commander = CommandFactory.initCommander();
        commander.parse(line.split(" "));
        JCommander parsed = commander.getCommands().get(commander.getParsedCommand());
        Command command = (Command) parsed.getObjects().get(0);
        command.execute(vcs);
    }

}
