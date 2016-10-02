package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Checkout branch or revision")
public class Checkout implements Command {
    @Parameter(description = "Name of branch or revision")
    private List<String> branch;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        vcs.checkout(branch.get(0));
    }
}
