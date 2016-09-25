package ru.spbau.mit.vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MergeTest {

    private VCS vcs;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        vcs = new VCS(folder.getRoot().getAbsolutePath());
    }

    @Test
    public void testMerge() throws IOException, VCSException {
        File file1 = folder.newFile("1");

        File file2 = folder.newFile("2");
        String initial = "initial 2";
        FileUtils.writeStringToFile(file2, initial);
        vcs.addFiles(Arrays.asList(file1.getAbsolutePath(), file2.getAbsolutePath()));
        vcs.commit("1");

        vcs.createBranch("branch");
        String changed = "changed 2";
        FileUtils.writeStringToFile(file2, changed);
        FileUtils.deleteQuietly(file1);
        File file3 = folder.newFile("3");
        vcs.addFiles(Collections.singletonList(file3.getAbsolutePath()));
        vcs.commit("2");

        vcs.checkout("master");
        Assert.assertTrue(file1.exists());
        Assert.assertTrue(file2.exists());
        List<String> lines = FileUtils.readLines(file2);
        Assert.assertEquals(initial, lines.get(0));
        Assert.assertFalse(file3.exists());

        vcs.merge("branch", null);
        Assert.assertFalse(file1.exists());
        Assert.assertTrue(file2.exists());
        lines = FileUtils.readLines(file2);
        Assert.assertEquals(changed, lines.get(0));
        Assert.assertTrue(file3.exists());
    }

    @Test(expected = VCSException.class)
    public void testCantMerge() throws IOException, VCSException {
        File file = folder.newFile("1");
        FileUtils.writeStringToFile(file, "initial 1");

        vcs.addFiles(Collections.singletonList(file.getAbsolutePath()));
        vcs.commit("1");

        vcs.createBranch("branch");
        FileUtils.writeStringToFile(file, "changed 1");
        vcs.commit("2");

        vcs.checkout("master");
        FileUtils.writeStringToFile(file, "again changed 1");
        vcs.commit("3");
        vcs.merge("branch", null);
    }
}
