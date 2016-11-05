package torrent.tracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import torrent.client.TorrentFile;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackerStateSaver {
    public static final String PATH = "tracker_files.json";

    public static List<FileInfo> getFiles(String home) {
        File stored = new File(home, PATH);
        if (!stored.exists()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        try (Reader reader = new FileReader(stored)) {
            Type type = new TypeToken<List<FileInfo>>() { }.getType();
            //noinspection unchecked
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            System.err.println("Failed to load saved files");
            return new ArrayList<>();
        }
    }

    public static void saveFiles(String home, List<FileInfo> files) throws IOException {
        File stored = new File(home, PATH);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (PrintWriter writer = new PrintWriter(stored)) {
            String json = gson.toJson(files);
            writer.println(json);
        }
    }
}
