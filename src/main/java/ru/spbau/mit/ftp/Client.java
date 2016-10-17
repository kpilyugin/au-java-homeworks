package ru.spbau.mit.ftp;

import ru.spbau.mit.ftp.query.File;
import ru.spbau.mit.ftp.query.FileInfo;
import ru.spbau.mit.ftp.query.Type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private final String ip;
    private final int port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connected to " + ip + " on port " + port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void disconnect() throws IOException {
        output.writeInt(0);
        socket.close();
        socket = null;
    }

    public FileInfo[] executeList(String path) throws IOException {
        output.writeInt(Type.LIST);
        output.writeUTF(path);
        output.flush();

        int numFiles = input.readInt();
        FileInfo[] response = new FileInfo[numFiles];
        for (int i = 0; i < numFiles; i++) {
            String name = input.readUTF();
            boolean isDirectory = input.readBoolean();
            response[i] = new FileInfo(name, isDirectory);
        }
        return response;
    }

    public File executeGet(String path) throws IOException {
        output.writeInt(Type.GET);
        output.writeUTF(path);
        output.flush();

        int size = input.readInt();
        if (size == 0) {
            return new File(0, null);
        } else {
            byte[] data = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            input.read(data);
            return new File(size, data);
        }
    }
}
