package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;

@Parameters(commandDescription = "Print status of changed/added/deleted files")
public class Status implements Command {

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        vcs.getRepository().makeSnapshot();
    }
}
