package ru.spbau.mit.vcs;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class CleanTest extends VCSTest {
    @Test
    public void testClean() throws IOException, VCSException {
        File file1 = folder.newFile("1");
        File file2 = folder.newFile("2");
        vcs.getRepository().addFiles(file1.getAbsolutePath());

        vcs.getRepository().clean();
        Assert.assertTrue(file1.exists());
        Assert.assertFalse(file2.exists());
    }
}
