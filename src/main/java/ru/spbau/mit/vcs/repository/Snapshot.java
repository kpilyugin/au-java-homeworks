package ru.spbau.mit.vcs.repository;

import java.util.HashMap;
import java.util.Map;

public class Snapshot {
    private final Map<String, String> contentMap = new HashMap<>();

    public void addFile(String file, String hash) {
        contentMap.put(file, hash);
    }

    public void writeRevision(int revision) {

    }
}
