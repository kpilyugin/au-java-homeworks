package ru.spbau.mit.vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResetTest extends VCSTest {

    @Test
    public void testReset() throws IOException, VCSException {
        File file = folder.newFile("1");
        FileUtils.writeStringToFile(file, "initial");
        vcs.getRepository().addFiles(file.getAbsolutePath());
        vcs.commit("1");

        String changed = "changed";
        FileUtils.writeStringToFile(file, changed);
        vcs.getRepository().resetFile(file.getAbsolutePath(), vcs.getCurrentRevision().getNumber());
        List<String> lines = FileUtils.readLines(file);
        Assert.assertEquals(changed, lines.get(0));
    }
}
