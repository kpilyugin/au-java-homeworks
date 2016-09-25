package ru.spbau.mit.vcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.spbau.mit.vcs.VCS;

import java.io.*;

public class VCSSerializer {
    public static VCS readEnv(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Reader reader = new FileReader(path)) {
            return gson.fromJson(reader, VCS.class);
        } catch (Exception e) {
            System.out.println("Failed reading data from json");
            return new VCS();
        }
    }

    public static void saveEnv(VCS context, String path) throws IOException {
        Gson gson = new Gson();

        try (PrintWriter writer = new PrintWriter(path)) {
            String json = gson.toJson(context);
            writer.println(json);
        }
    }
}
