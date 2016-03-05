package ru.spbau.mit.pilyugin;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class TrieSerializationTest {

    private final Random random = new Random();

    private Trie trie;
    private Trie copy;

    private void copyTrie() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        trie.serialize(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray(), 0, out.size());
        copy.deserialize(in);
    }

    @Before
    public void setUp() {
        trie = new TrieImpl();
        copy = new TrieImpl();
    }

    @Test
    public void testEmpty() throws IOException {
        copyTrie();

        assertEquals(0, copy.size());
        assertFalse(copy.contains(""));
    }

    @Test
    public void testClear() throws IOException {
        copy.add("");
        copy.add("a");

        copyTrie();

        assertEquals(0, copy.size());
        assertFalse(copy.contains(""));
    }

    @Test
    public void testSimple() throws IOException {
        trie.add("");
        trie.add("a");

        copyTrie();

        assertEquals(2, copy.size());
        assertTrue(copy.contains(""));
        assertTrue(copy.contains("a"));
        assertFalse(copy.contains("b"));
    }

    @Test
    public void testRandom() throws IOException {
        int n = 100;

        List<String> content = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String s = generateRandomString(10);
            content.add(s);
            trie.add(s);
        }

        copyTrie();

        assertEquals(trie.size(), copy.size());
        for (String element : content) {
            assertTrue(copy.contains(element));
        }
        for (int i = 0; i < n; i++) {
            String s = generateRandomString(2);
            assertEquals(trie.howManyStartsWithPrefix(s), copy.howManyStartsWithPrefix(s));
        }
    }

    private String generateRandomString(int length) {
        String result = "";
        int size = random.nextInt(length);
        for (int i = 0; i < size; i++) {
            result += (char) (97 + random.nextInt(10));
        }
        return result;
    }
}