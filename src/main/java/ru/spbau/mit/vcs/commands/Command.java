package ru.spbau.mit.vcs.commands;

import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;

public interface Command {
    void execute(VCS vcs) throws VCSException, IOException;
}
