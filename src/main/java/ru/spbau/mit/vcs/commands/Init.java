package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;

@Parameters(commandDescription = "Initialize repository in working directory")
public class Init implements Command {
    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        throw new VCSException("VCS already initialized");
    }
}
