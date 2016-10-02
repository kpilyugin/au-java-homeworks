package ru.spbau.mit.vcs;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class RemoveTest extends VCSTest {
    @Test
    public void testRemove() throws IOException, VCSException {
        File file1 = folder.newFile("1");
        vcs.getRepository().addFiles(file1.getAbsolutePath());

        vcs.getRepository().removeFiles(file1.getAbsolutePath());
        Assert.assertFalse(file1.exists());
    }
}
