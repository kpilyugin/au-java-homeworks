package ru.spbau.mit.vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.mit.vcs.revision.Revision;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CommitTest extends VCSTest {

    @Test
    public void testCommit() throws IOException, VCSException {
        File file = folder.newFile("1");
        FileUtils.writeStringToFile(file, "sample");
        vcs.getRepository().addFiles(file.getAbsolutePath());
        vcs.commit("1");
        Revision revision = vcs.getCurrentRevision();
        assertEquals("1", revision.getCommitMessage());
        assertEquals(1, revision.getNumber());
    }
}
