package ru.spbau.mit.vcs;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class VCSTest {
    protected VCS vcs;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException, VCSException {
        vcs = new VCS(folder.getRoot().getAbsolutePath());
        vcs.commit("Initial commit");
    }
}
