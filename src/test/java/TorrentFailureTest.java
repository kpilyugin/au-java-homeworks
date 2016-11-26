import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import torrent.client.TorrentClient;
import torrent.exception.DownloadException;
import torrent.tracker.TorrentTracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class TorrentFailureTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    TorrentTracker tracker;
    TorrentClient client1;
    TorrentClient client2;

    @After
    public void dispose() throws Exception {
        if (tracker != null) {
            tracker.end();
        }
        if (client1 != null) {
            client1.end();
        }
        if (client2 != null) {
            client2.end();
        }
    }

    @Test(expected = ConnectException.class)
    public void testNoTracker() throws Exception {
        InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getLocalHost(), TorrentTracker.PORT);
        client1 = new TorrentClient(trackerAddress, 0, tempFolder.newFolder().getPath());
    }

    @Test(expected = FileNotFoundException.class)
    public void testNoFile() throws Exception {
        tracker = new TorrentTracker(tempFolder.newFolder().getPath());
        InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getLocalHost(), TorrentTracker.PORT);
        client1 = new TorrentClient(trackerAddress, 0, tempFolder.newFolder().getPath());
        client1.getFile(1);
    }

    @Test(expected = DownloadException.class)
    public void testNoSeed() throws Exception {
        tracker = new TorrentTracker(tempFolder.newFolder().getPath());

        InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getLocalHost(), TorrentTracker.PORT);
        client1 = new TorrentClient(trackerAddress, 0, tempFolder.newFolder().getPath());
        File file = new File(tempFolder.newFolder(), "1.txt");
        FileUtils.writeStringToFile(file, "1", Charset.defaultCharset());
        client1.addFile(file);
        client1.end();
        client1 = null;

        client2 = new TorrentClient(trackerAddress, 0, tempFolder.newFolder().getPath());
        client2.getFile(1);
    }
}
