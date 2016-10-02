package ru.spbau.mit.vcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class VCSSerializer {
    public static VCS readEnv(File file) throws IOException {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, VCS.class);
        }
    }

    public static void saveEnv(VCS vcs, File file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (PrintWriter writer = new PrintWriter(file)) {
            String json = gson.toJson(vcs);
            writer.println(json);
        }
    }
}
