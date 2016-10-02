package ru.spbau.mit.vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.mit.vcs.revision.Revision;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class CommitTest {

    private VCS vcs;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        vcs = new VCS(folder.getRoot().getAbsolutePath());
    }

    @Test
    public void testCommit() throws IOException, VCSException {
        File file = folder.newFile("1");
        FileUtils.writeStringToFile(file, "sample");
        vcs.getRepository().addFiles(Collections.singletonList(file.getAbsolutePath()));
        vcs.commit("1");
        Revision revision = vcs.getCurrentRevision();
        assertEquals("1", revision.getCommitMessage());
        assertEquals(1, revision.getNumber());
    }
}
