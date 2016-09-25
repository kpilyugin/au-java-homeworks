package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Merge branch into current branch")
public class Merge implements Command {
    @Parameter(description = "Name of branch to merge")
    private List<String> branch;

    @Parameter(names = "-m", description = "Commit message")
    private String message;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        vcs.merge(branch.get(0), message);
    }
}
