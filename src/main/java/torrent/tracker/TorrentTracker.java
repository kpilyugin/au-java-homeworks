package torrent.tracker;

import torrent.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TorrentTracker extends Server {

    public static final int PORT = 8081;
    private static final Logger LOGGER = Logger.getLogger(TorrentTracker.class.getName());

    private int maxUsedId;
    private final String homeDir;
    private final List<FileInfo> files;
    private final Set<ClientInfo> activeClients = new HashSet<>();

    public TorrentTracker() {
        this(System.getProperty("user.dir"));
    }

    public TorrentTracker(String homeDir) {
        this.homeDir = homeDir;
        files = TrackerStateSaver.getFiles(homeDir);
        maxUsedId = files.isEmpty() ? 0 : files.get(files.size() - 1).getId();
    }

    @Override
    protected synchronized void processRequest(InetAddress address, DataInputStream input, DataOutputStream output) throws IOException {
        byte type = input.readByte();
        switch (type) {
            case TrackerRequests.LIST:
                listFiles(output);
                break;
            case TrackerRequests.UPLOAD:
                int id = addFile(input.readUTF(), input.readLong());
                output.writeInt(id);
                break;
            case TrackerRequests.SOURCES:
                listSources(input.readInt(), output);
                break;
            case TrackerRequests.UPDATE:
                processUpdate(address, input, output);
        }
    }

    @Override
    public void end() throws IOException {
        super.end();
        TrackerStateSaver.saveFiles(homeDir, files);
    }

    private void listFiles(DataOutputStream output) throws IOException {
        output.writeInt(files.size());
        for (FileInfo file : files) {
            file.writeTo(output);
        }
    }

    private int addFile(String name, long size) throws IOException {
        int id = maxUsedId + 1;
        maxUsedId++;
        files.add(new FileInfo(id, name, size));
        LOGGER.info("Added file: name = " + name + ", id = " + id);
        return id;
    }

    private void listSources(int id, DataOutputStream output) throws IOException {
        LOGGER.info("List sources for file: id = " + id);
        List<ClientInfo> sources = activeClients.stream()
                .filter(client -> client.hasFile(id) && client.isActive())
                .collect(Collectors.toList());
        output.writeInt(sources.size());
        for (ClientInfo client : sources) {
            client.writeTo(output);
        }
    }

    private void processUpdate(InetAddress address, DataInputStream input, DataOutputStream output) throws IOException {
        short port = input.readShort();
        LOGGER.info("Updating client: ip = " + Arrays.toString(address.getAddress()) + ", port = " + port);
        ClientInfo client = new ClientInfo(address.getAddress(), port);
        activeClients.remove(client);
        int numFiles = input.readInt();
        for (int i = 0; i < numFiles; i++) {
            int id = input.readInt();
            client.addFile(id);
        }
        activeClients.add(client);
        output.writeBoolean(true);
    }
}
