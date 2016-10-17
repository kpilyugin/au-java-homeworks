package ru.spbau.mit.ftp.query;

public class ServerFile {
    private final int size;
    private final byte[] data;

    public ServerFile(int size, byte[] data) {
        this.size = size;
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }
}
