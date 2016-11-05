package torrent.tracker;

import java.io.IOException;
import java.util.Scanner;

import static torrent.tracker.TorrentTracker.PORT;

public class TrackerMain {
    public static void main(String[] args) throws IOException {
        TorrentTracker tracker = new TorrentTracker();
        tracker.start(PORT);
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("exit")) {
                    tracker.end();
                    return;
                }
            }
        }
    }
}
