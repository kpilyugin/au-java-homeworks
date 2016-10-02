package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;

@Parameters(commandDescription = "Remove all files, not added to repository")
public class Clean implements Command {
    @Override
    public void execute(VCS vcs) throws VCSException, IOException {

    }
}
