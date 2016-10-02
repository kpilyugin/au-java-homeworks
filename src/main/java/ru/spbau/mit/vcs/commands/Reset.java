package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Reset status of file")
public class Reset implements Command {
    @Parameter(description = "File to reset")
    private List<String> files;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        String file = files.get(0);
        vcs.getRepository().resetFile(file, vcs.getCurrentRevision().getNumber());
    }
}
