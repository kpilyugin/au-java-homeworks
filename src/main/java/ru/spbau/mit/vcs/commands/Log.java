package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;
import ru.spbau.mit.vcs.revision.Revision;

@Parameters(commandDescription = "Print log of current branch")
public class Log implements VCSCommand {
    @Override
    public void execute(VCSContext context) throws VCSException {
        System.out.println("Log for branch " + context.getCurrentBranch().getName());
        Revision revision = context.getCurrentRevision();
        while (revision != null && revision.getNumber() > 0) {
            System.out.format("%d: %s\n", revision.getNumber(), revision.getCommitMessage());
            revision = context.getRevision(revision.getPrevious());
        }
    }
}
