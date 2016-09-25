package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Parameters(commandDescription = "Commit changes to repository")
public class Commit implements Command {
    @Parameter(description = "Commit message")
    private List<String> message;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        String msg = message.stream().collect(Collectors.joining(" "));
        vcs.commit(msg);
    }
}
