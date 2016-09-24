package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;

import java.util.List;

@Parameters(commandDescription = "Checkout branch or revision")
public class Checkout implements VCSCommand {
    @Parameter(description = "Name of branch or revision")
    private List<String> branch;

    @Override
    public void execute(VCSContext context) throws VCSException {
        context.checkout(branch.get(0));
    }
}
