package torrent.client;

import torrent.tracker.FileInfo;

import java.util.HashSet;
import java.util.Set;

public class TorrentFile {

    public static final int PART_SIZE = 1024;

    private final String name;
    private final long size;
    private final int id;
    private final transient Set<Integer> inProgress = new HashSet<>();
    private final Set<Integer> parts = new HashSet<>();

    @SuppressWarnings("unused")
    public TorrentFile() {
        this(null, 0, 0);
    }

    public TorrentFile(String name, long size, int id) {
        this.name = name;
        this.size = size;
        this.id = id;
    }

    public static TorrentFile createEmpty(FileInfo info) {
        return new TorrentFile(info.getName(), info.getSize(), info.getId());
    }

    public static TorrentFile createFull(String name, long size, int id) {
        TorrentFile file = new TorrentFile(name, size, id);
        int numParts = file.totalParts();
        for (int part = 0; part < numParts; part++) {
            file.addPart(part);
        }
        return file;
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

    private int totalParts() {
        return (int) Math.ceil((double) size / PART_SIZE);
    }

    public String getName() {
        return name;
    }

    public int getPartSize(int part) {
        if (part < totalParts() - 1) {
            return PART_SIZE;
        }
        return (int) (size - part * PART_SIZE);
    }

    @Override
    public String toString() {
        return "[name: " + name + ", id: " + id + ", inProgress = " + inProgress + "]";
    }
}
