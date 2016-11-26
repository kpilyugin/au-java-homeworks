package torrent.exception;

public class NoSeedFoundException extends DownloadException {
    public NoSeedFoundException(String msg) {
        super(msg);
    }
}
