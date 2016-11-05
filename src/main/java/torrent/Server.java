package torrent;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public abstract class Server implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    protected ServerSocket serverSocket;
    protected int localPort;

    public void start(int port) throws IOException {
        LOGGER.info("Started server at port " + port);
        localPort = port;
        executor.submit(this);
    }

    public void end() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
        executor.shutdown();
    }

    protected abstract void processRequest(InetAddress address, DataInputStream input, DataOutputStream output) throws IOException;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(() -> handleConnection(socket));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    LOGGER.info("Socket is closed");
                    return;
                } else {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    private void handleConnection(Socket socket) {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            while (socket.isConnected() && !socket.isClosed()) {
                processRequest(socket.getInetAddress(), input, output);
                output.flush();
            }
        } catch (EOFException ignored) {
            // disconnected
        } catch (IOException e) {
            LOGGER.warning("Error in connection: ");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
