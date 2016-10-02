package ru.spbau.mit.vcs;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BranchTest {

    private static final String BRANCH = "branch";

    private VCS vcs;

    @Before
    public void setUp() {
        vcs = new VCS();
    }

    @Test
    public void testDefault() {
        assertEquals(1, vcs.getBranches().size());
        assertEquals(VCS.DEFAULT_BRANCH, vcs.getCurrentBranch().getName());
        assertEquals(0, vcs.getCurrentBranch().getRevision());
    }

    @Test
    public void testAdd() throws VCSException, IOException {
        vcs.createBranch(BRANCH);
        assertEquals(2, vcs.getBranches().size());
        assertEquals(BRANCH, vcs.getCurrentBranch().getName());
    }

    @Test
    public void testDelete() throws VCSException {
        vcs.createBranch(BRANCH);
        vcs.deleteBranch(BRANCH);
        assertEquals(1, vcs.getBranches().size());
    }

    @Test(expected = VCSException.class)
    public void testAlreadyAdded() throws VCSException {
        vcs.createBranch(BRANCH);
        vcs.createBranch(BRANCH);
    }

    @Test(expected = VCSException.class)
    public void testCantDelete() throws VCSException {
        vcs.deleteBranch(BRANCH);
    }
}
