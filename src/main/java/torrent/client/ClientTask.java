package torrent.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ClientTask {
    void process(DataInputStream input, DataOutputStream output) throws IOException;
}
