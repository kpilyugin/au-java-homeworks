package ru.spbau.mit.ftp;

import org.apache.commons.io.IOUtils;
import ru.spbau.mit.ftp.query.Type;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket socket;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                if (socket == null || socket.isClosed()) {
                    System.out.println("Socket is closed");
                    return;
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void handleConnection(Socket connection) {
        try {
            System.out.println("New connection: " + connection);
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
            System.out.println("Disconnected from " + connection);
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
            output.writeLong(0);
        } else {
            output.writeLong(file.length());
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copyLarge(fis, output);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: <port>");
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
