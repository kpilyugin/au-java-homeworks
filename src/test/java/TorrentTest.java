import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import torrent.client.TorrentClient;
import torrent.tracker.FileInfo;
import torrent.tracker.TorrentTracker;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

public class TorrentTest {

    private static final int RANDOM_TESTS = 10;

    private File trackerFolder;
    private TorrentTracker tracker;
    private File folder1;
    private TorrentClient client1;
    private File folder2;
    private TorrentClient client2;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        trackerFolder = folder.newFolder();
        tracker = new TorrentTracker(trackerFolder.getPath());
        tracker.start(TorrentTracker.PORT);

        InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getLocalHost(), TorrentTracker.PORT);
        folder1 = folder.newFolder();
        client1 = new TorrentClient(trackerAddress, 12345, folder1.getPath());
        folder2 = folder.newFolder();
        client2 = new TorrentClient(trackerAddress, 12346, folder2.getPath());
    }

    @After
    public void tearDown() throws IOException {
        tracker.end();
        client1.end();
        client2.end();
    }

    @Test
    public void testAdd() throws IOException {
        File file = new File(folder1, "1.txt");
        FileUtils.writeStringToFile(file, "sampleFile", Charset.defaultCharset());

        List<FileInfo> files = client1.listFiles();
        System.out.println(files.size());
        Assert.assertTrue(files.isEmpty());

        client1.addFile(file.getName());
        files = client1.listFiles();
        Assert.assertEquals(1, files.size());

        FileInfo info = files.get(0);
        Assert.assertEquals(file.getName(), info.getName());
        Assert.assertEquals(file.length(), info.getSize());
        Assert.assertEquals(1, info.getId());
    }

    @Test
    public void testSaveState() throws IOException {
        File file = new File(folder1, "1.txt");
        FileUtils.writeStringToFile(file, "sampleFile", Charset.defaultCharset());
        client1.addFile(file.getName());

        tracker.end();
        tracker = new TorrentTracker(trackerFolder.getPath());
        tracker.start(TorrentTracker.PORT);

        FileInfo info = client2.listFiles().get(0);
        Assert.assertEquals(file.getName(), info.getName());
        Assert.assertEquals(file.length(), info.getSize());
        Assert.assertEquals(1, info.getId());
    }

    @Test
    public void testLoadShortFile() throws Exception {
        testLoadFile(1000);
    }

    @Test
    public void testRandom() throws Exception {
        Random random = new Random();
        for (int i = 0; i < RANDOM_TESTS; i++) {
            testLoadFile(random.nextInt(1000000));
        }
    }

    private void testLoadFile(int length) throws Exception {
        String name = new BigInteger(100, new Random()).toString(32);
        File file = new File(folder1, name);

        String content = new BigInteger(length * 5, new Random()).toString(32);
        FileUtils.writeStringToFile(file, content, Charset.defaultCharset());

        client1.addFile(name);
        List<FileInfo> files = client2.listFiles();
        FileInfo last = files.get(files.size() - 1);
        client2.getFile(last.getId());

        File loaded = new File(folder2, name);
        String result = FileUtils.readFileToString(loaded, Charset.defaultCharset());
        Assert.assertEquals(result, content);
    }
}
