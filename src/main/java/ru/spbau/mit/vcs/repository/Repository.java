package ru.spbau.mit.vcs.repository;

import org.apache.commons.io.FileUtils;
import ru.spbau.mit.vcs.VCSException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Repository {
    private final String workingDir;
    private final Set<String> trackedFiles = new HashSet<>();

    public Repository(String workingDir) {
        this.workingDir = workingDir;
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
                FileUtil.listExternalFiles(file).forEach(consumer);
            } else {
                consumer.accept(file);
            }
        }
    }

    private void addFile(File file) {
        String relativePath = getRelativePath(file, workingDir);
        trackedFiles.add(relativePath);
    }

    private void removeFile(File file) {
        String relativePath = getRelativePath(file, workingDir);
        trackedFiles.remove(relativePath);
        FileUtils.deleteQuietly(file);
    }

    public Snapshot getCurrentSnapshot() {
        Collection<File> allFiles = FileUtil.listExternalFiles(new File(workingDir));
        Snapshot snapshot = new Snapshot();
        allFiles.forEach(file -> {
            String relativePath = getRelativePath(file, workingDir);
            snapshot.addFile(relativePath, FileUtil.calculateSha1(file));
        });
        return snapshot;
    }

    public Snapshot getSnapshot(int revision) throws IOException {
        File file = getSnapshotFile(revision);
        return SnapshotSerializer.readSnapshot(file);
    }

    public void writeRevision(int revision) throws IOException {
        Snapshot snapshot = new Snapshot();
        for (String file : trackedFiles) {
            File srcFile = new File(workingDir, file);
            if (srcFile.exists()) {
                String hash = FileUtil.calculateSha1(srcFile);
                snapshot.addFile(file, hash);
                File dataFile = new File(getDataDirectory(), hash);
                if (!dataFile.exists()) {
                    FileUtils.copyFile(srcFile, dataFile);
                }
            }
        }
        SnapshotSerializer.writeSnapshot(snapshot, getSnapshotFile(revision));
    }

    public void checkoutRevision(int revision) throws IOException {
        Snapshot snapshot = SnapshotSerializer.readSnapshot(getSnapshotFile(revision));
        trackedFiles.addAll(snapshot.keySet());
        for (String file : trackedFiles) {
            String hash = snapshot.get(file);
            FileUtils.copyFile(new File(getDataDirectory(), hash), new File(workingDir, file));
        }
    }

    /**
     * Three-way merge algorithm: if only one revision is changed comparing to base (or both changes are equal),
     * then this change is applied, elsewise, there is a conflict that needs to be resolved.
     */
    public void merge(int fromRevision, int toRevision, int baseRevision, int next) throws VCSException, IOException {
        Snapshot from = getSnapshot(fromRevision);
        Snapshot to = getSnapshot(toRevision);
        Snapshot base = getSnapshot(baseRevision);

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(from.keySet());
        allFiles.addAll(to.keySet());
        allFiles.addAll(base.keySet());

        Snapshot result = new Snapshot();
        for (String file : allFiles) {
            boolean changedTo = !Objects.equals(to.get(file), base.get(file));
            boolean changedFrom = !Objects.equals(from.get(file), base.get(file));
            boolean changeDiffers = !Objects.equals(from.get(file), to.get(file));

            if (changedTo && changedFrom && changeDiffers) {
                throw new VCSException("Merge conflict: file " + file + " differs");
            }
            if (changedFrom) { // changed or deleted
                if (from.contains(file)) {
                    result.addFile(file, from.get(file));
                }
                continue;
            }
            if (to.contains(file)) {
                result.addFile(file, to.get(file));
            }
        }
        SnapshotSerializer.writeSnapshot(result, getSnapshotFile(next));
    }

    public Set<String> getTrackedFiles() {
        return trackedFiles;
    }

    private File getSnapshotFile(int revision) {
        return new File(workingDir, String.valueOf(revision) + ".json");
    }

    private File getDataDirectory() {
        return new File(workingDir, "data");
    }

    private static String getRelativePath(File file, String directory) {
        return file.getAbsolutePath().substring(directory.length() + 1);
    }
}
