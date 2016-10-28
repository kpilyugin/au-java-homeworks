package ru.spbau.mit.ftp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.spbau.mit.ftp.query.FileInfo;
import ru.spbau.mit.ftp.query.ServerFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Random;

public class FtpTest {

    private static final int PORT = 40000;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private Server server;
    private Client client;

    @Before
    public void setUp() throws IOException {
        server = new Server(PORT);
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        client = new Client("localhost", PORT);
        client.connect();
    }

    @After
    public void tearDown() throws IOException {
        client.disconnect();
        server.end();
    }

    @Test
    public void testList() throws IOException {
        File directory = folder.newFolder();

        FileInfo[] files = client.executeList(directory.getPath());
        Assert.assertEquals(0, files.length);

        File file = new File(directory.getPath(), "1.txt");
        boolean created = file.createNewFile();
        Assert.assertTrue(created);
        files = client.executeList(directory.getPath());
        Assert.assertEquals(1, files.length);
        Assert.assertEquals(file.getPath(), files[0].getPath());
        Assert.assertFalse(files[0].isDirectory());
    }

    @Test
    public void testGet() throws IOException {
        File file = folder.newFile();

        int size = 50;
        byte[] data = new byte[size];
        new Random().nextBytes(data);
        FileUtils.writeByteArrayToFile(file, data);
        ServerFile serverFile = client.executeGet(file.getPath());
        Assert.assertEquals(size, serverFile.getSize());
        byte[] serverData = IOUtils.toByteArray(new FileInputStream(serverFile.getFile()));
        Assert.assertArrayEquals(data, serverData);
    }

    @Test
    public void testNoDirectory() throws IOException {
        FileInfo[] files = client.executeList("noSuchDirectory");
        Assert.assertEquals(0, files.length);
    }

    @Test
    public void testNoFile() throws IOException {
        ServerFile file = client.executeGet("noSuchFile");
        Assert.assertEquals(0, file.getSize());
    }

    @Test
    public void testMultipleClients() throws IOException, InterruptedException {
        File file = folder.newFile();
        int size = 10;
        byte[] data = new byte[size];
        new Random().nextBytes(data);
        FileUtils.writeByteArrayToFile(file, data);

        int numClients = 10;
        for (int i = 0; i < numClients; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Client client = new Client("localhost", PORT);
                    client.connect();
                    ServerFile serverFile = client.executeGet(file.getPath());
                    Assert.assertEquals(size, serverFile.getSize());
                    byte[] serverData = IOUtils.toByteArray(new FileInputStream(serverFile.getFile()));
                    Assert.assertArrayEquals(data, serverData);
                    client.disconnect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            thread.join();
        }
    }
}