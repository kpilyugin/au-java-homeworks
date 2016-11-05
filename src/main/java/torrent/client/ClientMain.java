package torrent.client;

import torrent.tracker.FileInfo;
import torrent.tracker.TorrentTracker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ClientMain {
    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        if (args.length < 2) {
            System.out.println("Usage: <tracker_ip> <port>");
            return;
        }
        InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getByName(args[0]), TorrentTracker.PORT);
        int port = Integer.parseInt(args[1]);
        TorrentClient client = new TorrentClient(trackerAddress, port);

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cmd = line.split(" ");
                try {
                    switch (cmd[0]) {
                        case "list":
                            List<FileInfo> files = client.listFiles();
                            System.out.println("Number of files = " + files.size());
                            for (int i = 0; i < files.size(); i++) {
                                FileInfo file = files.get(i);
                                System.out.println((i + 1) + ": " + "id = " + file.getId() +
                                        ", name = " + file.getName() + ", size = " + file.getSize());
                            }
                            break;
                        case "add":
                            String fileName = cmd[1];
                            client.addFile(fileName);
                            break;
                        case "get":
                            int id = Integer.parseInt(cmd[1]);
                            client.getFile(id);
                            break;
                        case "exit":
                            client.end();
                            return;
                        default:
                            printUsage();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to execute command");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: list | add <file_name> | get <file_id> | exit");
    }
}
