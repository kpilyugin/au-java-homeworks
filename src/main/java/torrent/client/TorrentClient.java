package torrent.client;

import torrent.Server;
import torrent.exception.DownloadException;
import torrent.exception.NoSeedFoundException;
import torrent.tracker.FileInfo;
import torrent.tracker.TrackerRequests;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TorrentClient extends Server {

    private static final Logger LOGGER = Logger.getLogger(TorrentClient.class.getName());

    private static final int HEARTBEAT_PERIOD = 5;
    private static final int STAT = 1;
    private static final int GET = 2;

    private final Map<Integer, TorrentFile> files;
    private final FileManager fileManager;
    private final InetSocketAddress trackerAddress;
    private final String homeDir;

    public TorrentClient(InetSocketAddress trackerAddress, int port) 
            throws IOException, ExecutionException, InterruptedException {
        this(trackerAddress, port, System.getProperty("user.dir"));
    }

    public TorrentClient(InetSocketAddress trackerAddress, int port, String homeDir) 
            throws IOException, ExecutionException, InterruptedException {
        this.trackerAddress = trackerAddress;
        this.homeDir = homeDir;
        files = ClientStateSaver.getFiles(homeDir);
        fileManager = new FileManager();
        start(port);
        executor.scheduleAtFixedRate(() -> {
            try {
                update();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, 0, HEARTBEAT_PERIOD, TimeUnit.MINUTES);
        for (TorrentFile file : files.values()) {
            if (!file.isFull()) {
                try {
                    loadFile(file, null);
                } catch (DownloadException e) {
                    LOGGER.warning("File " + file + " is not fully loaded");
                }
            }
        }
    }

    @Override
    public void end() throws IOException {
        super.end();
        ClientStateSaver.saveFiles(homeDir, files);
    }

    public void update() throws IOException {
        connectToTracker(((input, output) -> {
            output.writeByte(TrackerRequests.UPDATE);
            output.writeShort(localPort);
            output.writeInt(files.size());
            for (Integer id : files.keySet()) {
                output.writeInt(id);
            }
            boolean updated = input.readBoolean();
            if (!updated) {
                LOGGER.warning("Failed to update");
            }
        }));
    }

    public Collection<TorrentFile> getFiles() {
        return files.values();
    }

    public boolean containsFile(FileInfo file) {
        return files.containsKey(file.getId());
    }

    public void addFile(String name) throws IOException {
        addFile(new File(homeDir, name));
    }

    public void addFile(File file) throws IOException {
        String name = file.getName();
        if (!file.exists()) {
            LOGGER.warning("File " + name + " does not exist.");
            return;
        }
        long size = file.length();
        connectToTracker((input, output) -> {
            output.writeByte(TrackerRequests.UPLOAD);
            output.writeUTF(name);
            output.writeLong(size);

            int id = input.readInt();
            files.put(id, TorrentFile.createFull(file, size, id));
        });
        update();
    }

    public List<FileInfo> listFiles() throws IOException {
        List<FileInfo> result = new ArrayList<>();
        connectToTracker((input, output) -> {
            output.writeByte(TrackerRequests.LIST);
            int numFiles = input.readInt();
            for (int i = 0; i < numFiles; i++) {
                result.add(new FileInfo(input.readInt(), input.readUTF(), input.readLong()));
            }
        });
        return result;
    }

    public void getFile(FileInfo info, File fileTo, LoadingHandler handler) 
            throws IOException, ExecutionException, InterruptedException, DownloadException {
        if (!files.containsKey(info.getId())) {
            TorrentFile file = TorrentFile.createEmpty(info, fileTo);
            files.put(info.getId(), file);
            loadFile(file, handler);
        }
    }

    public void getFile(int id) throws IOException, ExecutionException, InterruptedException, DownloadException {
        for (FileInfo info : listFiles()) {
            if (id == info.getId()) {
                getFile(info, new File(homeDir, info.getName()), null);
            }
        }
        LOGGER.warning("File with id = " + id + " not found.");
    }

    private void loadFile(TorrentFile file, LoadingHandler handler) 
            throws IOException, ExecutionException, InterruptedException, DownloadException {
        List<InetSocketAddress> seeds = getSeeds(file.getId());
        if (seeds.size() == 0) {
            throw new NoSeedFoundException(file.getName());
        }

        List<Future<?>> futures = new ArrayList<>();
        for (InetSocketAddress seed : seeds) {
            try {
                futures.add(executor.submit(new DownloadPartsTask(seed, file, handler)));
            } catch (Exception e) {
                LOGGER.warning("Seed " + seed + " is unavailable");
            }
        }
        for (Future<?> future : futures) {
            future.get();
        }
        if (file.isFull()) {
            LOGGER.info("Successfully loaded file " + file.getFile());
        } else {
            throw new DownloadException("Not fully loaded " + file.getName());
        }
    }

    private List<InetSocketAddress> getSeeds(int id) throws IOException {
        List<InetSocketAddress> seeds = new ArrayList<>();
        connectToTracker((input, output) -> {
            output.writeByte(TrackerRequests.SOURCES);
            output.writeInt(id);
            int numSeeds = input.readInt();
            for (int i = 0; i < numSeeds; i++) {
                byte[] address = new byte[4];
                for (int j = 0; j < 4; j++) {
                    address[j] = input.readByte();
                }
                short port = input.readShort();
                InetAddress inetAddress = InetAddress.getByAddress(address);
                InetSocketAddress seedAddress = new InetSocketAddress(inetAddress, port);
                boolean isLoopback = inetAddress.equals(InetAddress.getLoopbackAddress());
                if (!isLoopback || port != localPort) {
                    seeds.add(seedAddress);
                }
            }
        });
        return seeds;
    }

    private void connectToTracker(ClientTask task) throws IOException {
        connect(trackerAddress, task);
    }

    private static void connect(InetSocketAddress address, ClientTask task) throws IOException {
        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            task.process(input, output);
        }
    }

    @Override
    protected void processRequest(InetAddress address, DataInputStream input, DataOutputStream output) throws IOException {
        int type = input.readByte();
        switch (type) {
            case STAT: {
                int id = input.readInt();
                TorrentFile file = files.get(id);
                Set<Integer> parts = file.getParts();
                output.writeInt(parts.size());
                for (int part : parts) {
                    output.writeInt(part);
                }
                break;
            }
            case GET: {
                int id = input.readInt();
                int part = input.readInt();
                TorrentFile file = files.get(id);
                fileManager.readPart(file, part, output);
            }
        }
    }

    private class DownloadPartsTask implements Runnable {
        private final InetSocketAddress seed;
        private final TorrentFile file;
        private final LoadingHandler handler;

        public DownloadPartsTask(InetSocketAddress seed, TorrentFile file, LoadingHandler handler) {
            this.seed = seed;
            this.file = file;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                connect(seed, (input, output) -> {
                    int[] parts = collectParts(input, output);
                    for (int part : parts) {
                        if (!file.containsPart(part) && !file.isPartLoading(part)) {
                            file.startLoading(part);
                            loadPart(input, output, part);
                            if (handler != null) {
                                handler.onPartLoaded(file.getParts().size(), file.totalParts());
                            }
                        }
                    }
                    update();
                });
            } catch (ConnectException e) {
                LOGGER.warning("No connection to seed");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private int[] collectParts(DataInputStream input, DataOutputStream output) throws IOException {
            output.writeByte(STAT);
            output.writeInt(file.getId());
            int numParts = input.readInt();
            int[] parts = new int[numParts];
            for (int i = 0; i < numParts; i++) {
                parts[i] = input.readInt();
            }
            return parts;
        }

        private void loadPart(DataInputStream input, DataOutputStream output, int part) throws IOException {
            output.writeByte(GET);
            output.writeInt(file.getId());
            output.writeInt(part);
            fileManager.writePart(file, part, input);
            file.addPart(part);
        }
    }
}
