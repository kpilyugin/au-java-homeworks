package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;

import java.util.List;

@Parameters(commandDescription = "Commit changes to repository")
public class Commit implements VCSCommand {
    @Parameter(description = "Commit message")
    private List<String> message;

    @Override
    public void execute(VCSContext context) throws VCSException {
        context.commit(message.get(0));
    }
}
