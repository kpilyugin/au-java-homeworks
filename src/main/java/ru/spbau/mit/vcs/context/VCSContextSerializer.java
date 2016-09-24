package ru.spbau.mit.vcs.context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class VCSContextSerializer {
    public static VCSContext readContext(String path) {
        if (!new File(path).exists()) {
            return new VCSContext();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Reader reader = new FileReader(path)) {
            return gson.fromJson(reader, VCSContext.class);
        } catch (Exception e) {
            System.out.println("Failed reading data from json");
            return new VCSContext();
        }
    }

    public static void saveContext(VCSContext context, String path) throws IOException {
        Gson gson = new Gson();

        try (PrintWriter writer = new PrintWriter(path)) {
            String json = gson.toJson(context);
            writer.println(json);
        }
    }
}
