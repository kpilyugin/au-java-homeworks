package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Remove file from repository")
public class Remove implements Command {
    @Parameter(description = "Files or directories to remove")
    private List<String> files;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        vcs.getRepository().removeFiles(files.toArray(new String[files.size()]));
    }
}
