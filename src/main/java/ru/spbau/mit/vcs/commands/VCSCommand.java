package ru.spbau.mit.vcs.commands;

import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;

public interface VCSCommand {
    void execute(VCSContext context) throws VCSException;
}
