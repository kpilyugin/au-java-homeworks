package ru.spbau.mit.ftp.query;

public class FileInfo {
    private final String path;
    private final boolean isDirectory;

    public FileInfo(String path, boolean isDirectory) {
        this.path = path;
        this.isDirectory = isDirectory;
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
