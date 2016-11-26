package torrent.client;

import torrent.tracker.FileInfo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TorrentFile {

    public static final int PART_SIZE = 1024 * 1024;

    private final File file;
    private final long size;
    private final int id;
    private final transient Set<Integer> inProgress = new HashSet<>();
    private final Set<Integer> parts = new HashSet<>();

    @SuppressWarnings("unused")
    public TorrentFile() {
        this(null, 0, 0);
    }

    public TorrentFile(File file, long size, int id) {
        this.file = file;
        this.size = size;
        this.id = id;
    }

    public static TorrentFile createEmpty(FileInfo info, File file) {
        return new TorrentFile(file, info.getSize(), info.getId());
    }

    public static TorrentFile createFull(File file, long size, int id) {
        TorrentFile torrentFile = new TorrentFile(file, size, id);
        int numParts = torrentFile.totalParts();
        for (int part = 0; part < numParts; part++) {
            torrentFile.addPart(part);
        }
        return torrentFile;
    }

    public boolean isFull() {
        return parts.size() == totalParts();
    }

    public synchronized boolean containsPart(int part) {
        return parts.contains(part);
    }

    public synchronized boolean isPartLoading(int part) {
        return inProgress.contains(part);
    }

    public synchronized void startLoading(int part) {
        inProgress.add(part);
    }

    public synchronized void addPart(int part) {
        inProgress.remove(part);
        parts.add(part);
    }

    public Set<Integer> getParts() {
        return parts;
    }

    public int getId() {
        return id;
    }

    public int totalParts() {
        return (int) Math.ceil((double) size / PART_SIZE);
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return file.getName();
    }

    public int getPartSize(int part) {
        if (part >= totalParts()) {
            return 0;
        }
        if (part < totalParts() - 1) {
            return PART_SIZE;
        }
        return (int) (size - part * PART_SIZE);
    }

    @Override
    public String toString() {
        return "[file: " + file + ", id: " + id + ", inProgress = " + inProgress + "]";
    }
}
