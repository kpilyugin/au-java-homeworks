package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Add file or directory to tracked content")
public class Add implements Command {
    @Parameter(description = "Files or directories to add")
    private List<String> files;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        vcs.addTrackedFiles(files);
    }
}
