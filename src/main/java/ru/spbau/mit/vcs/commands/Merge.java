package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;

import java.util.List;

@Parameters(commandDescription = "Merge branch into current branch")
public class Merge implements VCSCommand {
    @Parameter(description = "Name of branch to merge")
    private List<String> branch;

    @Parameter(names = "-m", description = "Commit message")
    private String message;

    @Override
    public void execute(VCSContext context) throws VCSException {
    }
}
