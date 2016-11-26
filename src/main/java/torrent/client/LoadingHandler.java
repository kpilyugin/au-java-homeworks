package torrent.client;

public interface LoadingHandler {
    void onPartLoaded(int loaded, int total);
}
