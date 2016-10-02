package ru.spbau.mit.vcs.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class SnapshotSerializer {

    public static Snapshot readSnapshot(File file) throws IOException {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, Snapshot.class);
        }
    }

    public static void writeSnapshot(Snapshot snapshot, File file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(file)) {
            String json = gson.toJson(snapshot);
            writer.println(json);
        }
    }
}
