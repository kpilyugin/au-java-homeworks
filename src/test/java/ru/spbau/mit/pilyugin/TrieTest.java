package ru.spbau.mit.pilyugin;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Kirill Pilyugin
 */
public class TrieTest {
    private final Random random = new Random();

    @Test
    public void testAdd() {
        Trie trie = new TrieImpl();

        assertTrue(trie.add("a"));
        assertFalse(trie.add("a"));

        assertTrue(trie.add("bababababa"));
        assertTrue(trie.add("b"));
    }

    @Test
    public void testContains() {
        Trie trie = new TrieImpl();
        assertFalse(trie.contains("a"));

        trie.add("a");
        assertTrue(trie.contains("a"));

        String some = "thisissomestring";
        trie.add(some);
        assertTrue(trie.contains(some));
        assertFalse(trie.contains(some.substring(0, 5)));
        assertTrue(trie.contains("a"));
    }

    @Test
    public void testRemove() {
        Trie trie = new TrieImpl();
        String s = "abcdeedbca";
        String sub = s.substring(0, 4);

        trie.add(s);
        trie.add(sub);

        assertTrue(trie.remove(sub));
        assertTrue(trie.contains(s));
        assertFalse(trie.contains(sub));

        trie.add(sub);
        assertTrue(trie.remove(s));
        assertTrue(trie.contains(sub));
        assertFalse(trie.contains(s));
    }

    @Test
    public void testSize() {
        Trie trie = new TrieImpl();
        assertEquals(0, trie.size());

        trie.add("a");
        assertEquals(1, trie.size());
        trie.add("a");
        assertEquals(1, trie.size());
        trie.remove("a");

        int n = 1000;
        int size = 0;
        for (int i = 0; i < n; i++) {
            boolean added = trie.add(generateRandomString(10));
            if (added) {
                size++;
            }
        }
        assertEquals(size, trie.size());
    }

    @Test
    public void testHowManyStartsWithPrefix() {
        Trie trie = new TrieImpl();
        trie.add("a");
        assertEquals(1, trie.howManyStartsWithPrefix("a"));
        assertEquals(0, trie.howManyStartsWithPrefix("b"));

        int n = 10000;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String s = generateRandomString(10);
            list.add(s);
            trie.add(s);
        }
        for (int i = 0; i < 100; i++) {
            String prefix = generateRandomString(3);
            int expected = (int) list.stream().filter(s -> s.startsWith(prefix)).count();
            assertEquals(expected, trie.howManyStartsWithPrefix(prefix));
        }
    }

    private String generateRandomString(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += (char) (97 + random.nextInt(10));
        }
        return result;
    }
}