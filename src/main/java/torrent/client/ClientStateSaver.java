package torrent.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClientStateSaver {
    public static final String PATH = "files.json";

    public static Map<Integer, TorrentFile> getFiles(String home) {
        File stored = new File(home, PATH);
        if (!stored.exists()) {
            return new HashMap<>();
        }
        Gson gson = new Gson();
        try (Reader reader = new FileReader(stored)) {
            Type type = new TypeToken<Map<Integer, TorrentFile>>() { }.getType();
            //noinspection unchecked
            return (Map<Integer, TorrentFile>) gson.fromJson(reader, type);
        } catch (Exception e) {
            System.err.println("Failed to load saved files");
            return new HashMap<>();
        }
    }

    public static void saveFiles(String home, Map<Integer, TorrentFile> files) throws IOException {
        File stored = new File(home, PATH);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (PrintWriter writer = new PrintWriter(stored)) {
            String json = gson.toJson(files);
            writer.println(json);
        }
    }
}
