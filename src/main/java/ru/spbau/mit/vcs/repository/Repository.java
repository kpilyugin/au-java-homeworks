package ru.spbau.mit.vcs.repository;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Repository {
    private final String workingDir;
    private Set<String> trackedContent;

    public Repository(String workingDir) {
        this.workingDir = workingDir;
        trackedContent = new HashSet<>();
    }

    public void addFiles(List<String> paths) throws IOException {
        processFiles(paths, this::addFile);
    }

    public void removeFiles(List<String> paths) throws IOException {
        processFiles(paths, this::removeFile);
    }

    private void processFiles(List<String> paths, Consumer<File> consumer) {
        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                Collection<File> files = FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                files.forEach(consumer);
            } else {
                consumer.accept(file);
            }
        }
    }

    private void addFile(File file) {
        String relativePath = getRelativePath(file, workingDir);
        trackedContent.add(relativePath);
    }

    private void removeFile(File file) {
        String relativePath = getRelativePath(file, workingDir);
        trackedContent.remove(relativePath);
        FileUtils.deleteQuietly(file);
    }

    public Snapshot makeSnapshot() {
        Collection<File> allFiles = FileUtil.listExternalFiles(workingDir);
        Snapshot snapshot = new Snapshot();
        allFiles.forEach(file -> {
            String relativePath = getRelativePath(file, workingDir);
        });
        return new Snapshot();
    }

    public void writeRevision(int revision) throws IOException {
        String revisionPath = getRevisionPath(revision);
        for (String file : trackedContent) {
            File srcFile = new File(workingDir + file);
            if (srcFile.exists()) {
                FileUtils.copyFile(srcFile, new File(revisionPath + file));
            }
        }
    }

    public void checkoutRevision(int revision) throws IOException {
        String revisionPath = getRevisionPath(revision);
        File revisionDir = new File(revisionPath);
        for (String name : trackedContent) {
            FileUtils.deleteQuietly(new File(workingDir + name));
        }
        trackedContent.clear();
        if (!revisionDir.exists()) {
            return;
        }
        FileUtils.copyDirectory(revisionDir, new File(workingDir));
        Collection<File> files = FileUtils.listFiles(revisionDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
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
            for (String name : filesFrom) { // copy changed or new files to new revision
                boolean changedFrom = !contentEquals(name, from, base);
                if (changedFrom) {
                    boolean changedTo = !contentEquals(name, to, base);
                    boolean differs = !contentEquals(name, from, base);
                    if (!changedTo) {
                        FileUtils.copyFile(new File(getRevisionPath(from) + name), new File(getRevisionPath(next) + name));
                    }
                    if (changedTo && differs) {
                        // files are changed in both branches: need to resolve conflict
                        throw new VCSException("Merge conflict: file " + name + " differs");
                    }
                }
            }
            for (String name : filesBase) { // delete files, that were removed in branch we're merging from
                if (filesFrom.contains(name)) {
                    continue;
                }
                if (!filesTo.contains(name)) {
                    continue;
                }
                boolean changedTo = !contentEquals(name, to, base);
                if (!changedTo) {
                    FileUtils.deleteQuietly(new File(getRevisionPath(next) + name));
                }
            }
        } catch (VCSException | IOException e) {
            FileUtils.deleteDirectory(new File(getRevisionPath(next)));
            throw e;
        }
    }

    private static String getRelativePath(File file, String directory) {
        return file.getAbsolutePath().substring(directory.length() + 1);
    }

    private String getRevisionPath(int revision) {
        return String.format("%s/%s/%d", workingDir, VCS.FOLDER, revision);
    }

    private boolean contentEquals(String name, int revision1, int revision2) throws IOException {
        return FileUtils.contentEquals(new File(getRevisionPath(revision1) + name), new File(getRevisionPath(revision2) + name));
    }

    private List<String> getRevisionFiles(int revision) {
        String revisionPath = getRevisionPath(revision);
        File directory = new File(revisionPath);
        return FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                .stream()
                .map(file -> getRelativePath(file, revisionPath))
                .collect(Collectors.toList());
    }
}
