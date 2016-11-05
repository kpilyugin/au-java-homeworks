package torrent.tracker;

import java.io.DataOutputStream;
import java.io.IOException;

public class FileInfo {
    private final int id;
    private final String name;
    private final long size;

    public FileInfo(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public void writeTo(DataOutputStream output) throws IOException {
        output.writeInt(id);
        output.writeUTF(name);
        output.writeLong(size);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileInfo && ((FileInfo) obj).id == id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }
}
