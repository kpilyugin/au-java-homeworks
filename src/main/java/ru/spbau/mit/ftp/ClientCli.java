package ru.spbau.mit.ftp;

import ru.spbau.mit.ftp.query.ServerFile;
import ru.spbau.mit.ftp.query.FileInfo;

import java.io.IOException;
import java.util.Scanner;

public class ClientCli {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <ip> <port>");
            return;
        }
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        Client client = new Client(ip, port);
        client.connect();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split("\\s+");
                try {
                    switch (tokens[0]) {
                        case "exit":
                            client.disconnect();
                            System.exit(0);
                            break;
                        case "list":
                            dumpList(client.executeList(tokens[1]));
                            break;
                        case "get":
                            dumpFile(client.executeGet(tokens[1]));
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Failed to execute command");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void dumpList(FileInfo[] files) {
        System.out.println("Got " + files.length + " files");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ": " +
                    (files[i].isDirectory() ? "directory " : "file ") +
                    files[i].getPath());
        }
    }

    private static void dumpFile(ServerFile file) {
        System.out.println("File size = " + file.getSize() + ", saved at " + file.getFile().getAbsolutePath());
    }
}
