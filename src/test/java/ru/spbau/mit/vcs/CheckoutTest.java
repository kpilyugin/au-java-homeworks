package ru.spbau.mit.vcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class CheckoutTest {

    private VCS vcs;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        vcs = new VCS(folder.getRoot().getAbsolutePath());
    }

    @Test
    public void testCheckout() throws IOException, VCSException {
        File file1 = folder.newFile("1");
        vcs.createBranch("branch1");
        vcs.addFiles(Collections.singletonList(file1.getAbsolutePath()));
        vcs.commit("1");

        vcs.checkout("master");
        File file2 = folder.newFile("2");
        vcs.createBranch("branch2");
        vcs.addFiles(Collections.singletonList(file2.getAbsolutePath()));
        vcs.commit("2");

        vcs.checkout("master");
        Assert.assertFalse(file1.exists());
        Assert.assertFalse(file2.exists());

        vcs.checkout("branch1");
        Assert.assertTrue(file1.exists());
        Assert.assertFalse(file2.exists());

        vcs.checkout("branch2");
        Assert.assertFalse(file1.exists());
        Assert.assertTrue(file2.exists());
    }
}
