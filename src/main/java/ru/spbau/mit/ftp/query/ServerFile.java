package ru.spbau.mit.ftp.query;

import java.io.File;

public class ServerFile {
    private final long size;
    private final File file;

    public ServerFile(long size, File file) {
        this.size = size;
        this.file = file;
    }

    public long getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }
}
