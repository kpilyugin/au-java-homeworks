package ru.spbau.mit.vcs.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Snapshot {
    private final Map<String, String> contentMap = new HashMap<>();

    public void addFile(String file, String hash) {
        contentMap.put(file, hash);
    }

    public Set<String> keySet() {
        return contentMap.keySet();
    }

    public String get(String file) {
        return contentMap.get(file);
    }

    public boolean contains(String file) {
        return contentMap.containsKey(file);
    }
}
