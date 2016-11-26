package torrent.client;

import java.io.*;
import java.nio.channels.Channels;

public class FileManager {
    private final static int BUFFER_SIZE = 1024;

    public void readPart(TorrentFile torrentFile, int part, OutputStream output) throws IOException {
        long offset = (long) part * TorrentFile.PART_SIZE;
        try (RandomAccessFile file = new RandomAccessFile(torrentFile.getFile(), "r")) {
            file.seek(offset);
            copy(Channels.newInputStream(file.getChannel()), output, torrentFile.getPartSize(part));
        }
    }

    public void writePart(TorrentFile torrentFile, int part, InputStream input) throws IOException {
        long offset = (long) part * TorrentFile.PART_SIZE;
        try (RandomAccessFile file = new RandomAccessFile(torrentFile.getFile(), "rw")) {
            file.seek(offset);
            copy(input, Channels.newOutputStream(file.getChannel()), torrentFile.getPartSize(part));
        }
    }

    private static void copy(InputStream input, OutputStream output, int size) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        while (size > 0 && bytesRead >= 0) {
            bytesRead = input.read(buffer, 0, Math.min(size, BUFFER_SIZE));
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }
    }
}
