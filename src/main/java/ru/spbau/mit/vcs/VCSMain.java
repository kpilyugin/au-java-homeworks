package ru.spbau.mit.vcs;

import com.beust.jcommander.JCommander;
import ru.spbau.mit.vcs.commands.CommandFactory;
import ru.spbau.mit.vcs.commands.Command;
import ru.spbau.mit.vcs.commands.Init;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class VCSMain {
    private static final String ENV_FILE = "env.json";

    private File envFile;
    private VCS vcs;

    public VCSMain(String[] args) throws IOException {
        vcs = readEnvFromFile();
        if (args.length > 0) {
            try {
                executeCommand(args);
            } catch (Exception e) {
                System.out.println("Failed to execute command: " + e + " " + e.getMessage());
            }
        }
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    executeCommand(line.split(" "));
                } catch (Exception e) {
                    System.out.println("Failed to execute command: " + e.getMessage());
                    e.printStackTrace();
                }
                if (vcs != null) {
                    VCSSerializer.saveEnv(vcs, envFile);
                }
            }
        }
        if (vcs != null) {
            VCSSerializer.saveEnv(vcs, envFile);
        }
    }

    public static void main(String[] args) throws IOException {
        new VCSMain(args);
    }

    private VCS readEnvFromFile() throws IOException {
        File parentDir = new File(".");
        File vcsDir = new File(parentDir, VCS.FOLDER);
        while (!vcsDir.exists()) {
            String parent = parentDir.getParent();
            if (parent == null) {
                return null;
            }
            parentDir = new File(parent);
            vcsDir = new File(parentDir, VCS.FOLDER);
        }
        envFile = new File(vcsDir, ENV_FILE);
        return VCSSerializer.readEnv(envFile);
    }

    private void executeCommand(String[] args) throws VCSException, IOException {
        JCommander commander = CommandFactory.initCommander();
        commander.parse(args);
        JCommander parsed = commander.getCommands().get(commander.getParsedCommand());
        Command command = (Command) parsed.getObjects().get(0);
        if (command instanceof Init) {
            if (vcs != null) {
                throw new VCSException("VCS already initialized");
            }
            //noinspection ResultOfMethodCallIgnored
            new File(VCS.FOLDER).mkdirs();
            envFile = new File(VCS.FOLDER, ENV_FILE);
            vcs = new VCS();
            vcs.commit("Initial commit");
        } else {
            if (vcs == null) {
                throw new VCSException("VCS not initialized");
            }
            command.execute(vcs);
        }
    }

}
