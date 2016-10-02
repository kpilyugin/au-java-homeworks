package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;

@Parameters(commandDescription = "Prints usage")
public class Help implements Command {
    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        CommandFactory.initCommander().usage();
    }
}
