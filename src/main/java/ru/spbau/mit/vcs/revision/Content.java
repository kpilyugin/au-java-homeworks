package ru.spbau.mit.vcs.revision;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Content {
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private Set<String> trackedContent;

    public Content() {
        trackedContent = new HashSet<>();
    }

    public void addContent(List<String> paths) throws IOException {
        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                Collection<File> files = FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                for (File f : files) {
                    String relativePath = getRelativePath(f, WORKING_DIR);
                    trackedContent.add(relativePath);
                }
            } else {
                String relativePath = getRelativePath(file, WORKING_DIR);
                trackedContent.add(relativePath);
            }
        }
    }

    public void writeRevision(int revision) throws IOException {
        String revisionPath = getRevisionPath(revision);
        for (String file : trackedContent) {
            System.out.println("Copying " + (WORKING_DIR + file) + " to " + (revisionPath + file));
            File srcFile = new File(WORKING_DIR + file);
            if (srcFile.exists()) {
                FileUtils.copyFile(srcFile, new File(revisionPath + file));
            }
        }
    }

    public void checkoutRevision(int revision) throws IOException {
        String revisionPath = getRevisionPath(revision);
        File revisionDir = new File(revisionPath);
        FileUtils.copyDirectory(revisionDir, new File(WORKING_DIR));
        Collection<File> files = FileUtils.listFiles(revisionDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        trackedContent.clear();
        for (File file : files) {
            String relativePath = file.getAbsolutePath().substring(revisionPath.length());
            trackedContent.add(relativePath);
        }
    }

    public void merge(int from, int to, int base, int next) throws VCSException, IOException {
        List<String> filesFrom = getRevisionFiles(from);
        List<String> filesTo = getRevisionFiles(to);
        List<String> filesBase = getRevisionFiles(base);

        try {
            FileUtils.copyDirectory(new File(getRevisionPath(to)), new File(getRevisionPath(next)));
            for (String name : filesFrom) {
                boolean changedFrom = !contentEquals(name, from, base);
                if (changedFrom) {
                    System.out.println("File " + name + "changed in from");
                    boolean changedTo = !contentEquals(name, to, base);
                    boolean differs = !contentEquals(name, from, base);
                    System.out.println("changedTo = " + changedTo + ", differs = " + differs);
                    if (!changedTo) {
                        FileUtils.copyFile(new File(getRevisionPath(from) + name), new File(getRevisionPath(next) + name));
                    }
                    if (changedTo && differs) {
                        throw new VCSException("Merge conflict: file " + name + " differs");
                    }
                }
            }
            for (String name : filesBase) {
                if (filesFrom.contains(name)) {
                    continue;
                }
                if (!filesTo.contains(name)) {
                    continue;
                }
                boolean changedTo = !contentEquals(name, to, base);
                if (!changedTo) {
                    System.out.println("deleting file " + name);
                    FileUtils.deleteQuietly(new File(getRevisionPath(next) + name));
                }
            }
        } catch (VCSException | IOException e) {
            FileUtils.deleteDirectory(new File(getRevisionPath(next)));
            throw e;
        }
    }

    private static String getRelativePath(File file, String directory) {
        return file.getAbsolutePath().substring(directory.length());
    }

    private static String getRevisionPath(int revision) {
        return String.format("%s/%s/%d", WORKING_DIR, VCS.FOLDER, revision);
    }

    private static boolean contentEquals(String name, int revision1, int revision2) throws IOException {
        return FileUtils.contentEquals(new File(getRevisionPath(revision1) + name), new File(getRevisionPath(revision2) + name));
    }

    private static List<String> getRevisionFiles(int revision) {
        String revisionPath = getRevisionPath(revision);
        File directory = new File(revisionPath);
        return FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                .stream()
                .map(file -> getRelativePath(file, revisionPath))
                .collect(Collectors.toList());
    }
}
