package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.JCommander;

public class CommandFactory {

    public static JCommander initCommander() {
        JCommander commander = new JCommander();
        commander.addCommand("add", new Add());
        commander.addCommand("commit", new Commit());
        commander.addCommand("checkout", new Checkout());
        commander.addCommand("branch", new Branch());
        commander.addCommand("log", new Log());
        commander.addCommand("merge", new Merge());
        return commander;
    }
}
