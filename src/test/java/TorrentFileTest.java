import org.junit.Assert;
import org.junit.Test;
import torrent.client.TorrentFile;
import torrent.tracker.FileInfo;

import java.io.File;

public class TorrentFileTest {
    @Test
    public void testEmpty() {
        TorrentFile file = TorrentFile.createEmpty(new FileInfo(1, "1", TorrentFile.PART_SIZE + 1), new File("1"));
        int part = 0;
        Assert.assertFalse(file.containsPart(part));
        file.startLoading(part);
        Assert.assertFalse(file.containsPart(part));
        Assert.assertTrue(file.isPartLoading(part));
        file.addPart(part);
        Assert.assertTrue(file.containsPart(part));
        Assert.assertFalse(file.isPartLoading(part));
        Assert.assertFalse(file.isFull());

        file.addPart(1);
        Assert.assertTrue(file.isFull());
    }

    @Test
    public void testFull() {
        TorrentFile file = TorrentFile.createFull(new File("1"), TorrentFile.PART_SIZE * 10, 2);
        Assert.assertTrue(file.containsPart(5));
        Assert.assertTrue(file.isFull());
    }

    @Test
    public void testPartSize() {
        TorrentFile file = TorrentFile.createEmpty(new FileInfo(1, "1", TorrentFile.PART_SIZE + 2), new File("1"));
        Assert.assertEquals(TorrentFile.PART_SIZE, file.getPartSize(0));
        Assert.assertEquals(2, file.getPartSize(1));
        Assert.assertEquals(0, file.getPartSize(2));
    }
}
