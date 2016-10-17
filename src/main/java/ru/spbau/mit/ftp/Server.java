package ru.spbau.mit.ftp;

import ru.spbau.mit.ftp.query.Type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket socket;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        socket = new ServerSocket(port);
        System.out.println("Started server on port " + port);
        run();
    }

    public void end() throws IOException {
        socket.close();
        executor.shutdown();
        socket = null;
    }

    private void run() {
        while (socket != null) {
            try {
                Socket connection = socket.accept();
                executor.submit(() -> handleConnection(connection));
            } catch (IOException e) {
                System.err.println("Failed to connect");
                e.printStackTrace();
            }
        }
    }

    private void handleConnection(Socket connection) {
        try {
            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            int type;
            while ((type = input.readInt()) != Type.DISCONNECT) {
                String path = input.readUTF();
                System.out.println("Got query: " + type + " " + path);
                try {
                    if (type == Type.GET) {
                        getFile(path, output);
                    } else {
                        listFiles(path, output);
                    }
                    output.flush();
                } catch (IOException e) {
                    output.writeInt(0);
                    output.flush();
                    throw e;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed processing query: ");
            e.printStackTrace();
        }
    }

    private static void listFiles(String directory, DataOutputStream output) throws IOException {
        File[] files = new File(directory).listFiles();
        if (files == null) {
            output.writeInt(0);
        } else {
            output.writeInt(files.length);
            for (File file : files) {
                output.writeUTF(file.getPath());
                output.writeBoolean(file.isDirectory());
            }
        }
    }

    private static void getFile(String path, DataOutputStream output) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            output.writeInt(0);
        } else {
            byte[] data = Files.readAllBytes(Paths.get(path));
            output.writeInt(data.length);
            output.write(data);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: <ip>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Server server = null;
        try {
            server = new Server(port);
            server.start();
        } finally {
            if (server != null) {
                //noinspection ThrowFromFinallyBlock
                server.end();
            }
        }
    }
}
