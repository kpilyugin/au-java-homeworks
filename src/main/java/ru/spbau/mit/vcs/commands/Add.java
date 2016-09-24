package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;

import java.util.List;

@Parameters(commandDescription = "Add file or directory to tracked content")
public class Add implements VCSCommand {
    @Parameter(description = "Files or directories to add")
    private List<String> files;

    @Override
    public void execute(VCSContext context) throws VCSException {
        context.addTrackedContent(files);
    }
}
