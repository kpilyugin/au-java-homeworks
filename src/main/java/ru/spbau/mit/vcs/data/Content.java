package ru.spbau.mit.vcs.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import ru.spbau.mit.vcs.VCS;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Content {
    private Set<String> trackedContent;

    public Content() {
        trackedContent = new HashSet<>();
    }

    public void addContent(List<String> paths) {
        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                Collection<File> files = FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                files.forEach(f -> trackedContent.add(f.getAbsolutePath()));
            } else {
                trackedContent.add(file.getAbsolutePath());
            }
        }
    }

    public void writeRevision(int revision) {
        System.out.println("Content: " + trackedContent);
    }

    public void checkoutRevision(int revision) {
        //FileUtils.copyDirectory();
    }

    private String getDirectory(int revision) {
        return VCS.FOLDER + "/" + revision;
    }
}
