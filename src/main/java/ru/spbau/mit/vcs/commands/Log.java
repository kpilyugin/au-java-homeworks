package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;
import ru.spbau.mit.vcs.revision.Revision;

import java.io.IOException;

@Parameters(commandDescription = "Print log of current branch")
public class Log implements Command {
    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        System.out.println("Log for branch " + vcs.getCurrentBranch().getName());
        Revision revision = vcs.getCurrentRevision();
        while (revision != null && revision.getNumber() > 0) {
            System.out.format("%d: %s\n", revision.getNumber(), revision.getCommitMessage());
            revision = vcs.getRevision(revision.getPrevious());
        }
    }
}
