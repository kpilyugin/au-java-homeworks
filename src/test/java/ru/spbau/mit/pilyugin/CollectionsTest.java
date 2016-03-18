package ru.spbau.mit.pilyugin;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CollectionsTest {

    private final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

    @Test
    public void map() throws Exception {
        List<Integer> expected = Arrays.asList(1, 4, 9, 16, 25);
        List<Integer> result = Collections.map(x -> x * x, list);
        assertTrue(result.equals(expected));
    }

    @Test
    public void filter() throws Exception {
        List<Integer> expected = Arrays.asList(2, 4);
        List<Integer> result = Collections.filter(x -> x % 2 == 0, list);
        assertTrue(result.equals(expected));
    }

    @Test
    public void takeWhile() throws Exception {
        List<Integer> expected = Arrays.asList(1, 2);
        List<Integer> result = Collections.takeWhile(x -> x < 3, list);
        assertTrue(result.equals(expected));
    }

    @Test
    public void takeUnless() throws Exception {
        List<Integer> expected = Arrays.asList(1, 2, 3);
        List<Integer> result = Collections.takeUnless(x -> x > 3, list);
        assertTrue(result.equals(expected));
    }

    @Test
    public void foldrEmpty() throws Exception {
        Object initial = new Object();
        assertEquals(initial, Collections.foldr(null, initial, EMPTY_LIST));
    }

    @Test
    public void foldr() throws Exception {
        assertEquals(15, (int) Collections.foldr((x, y) -> x + y, 0, list));

        int foldrResult = (1 - (2 - (3 - (4 - (5)))));
        assertEquals(foldrResult, (int) Collections.foldr((x, y) -> x - y, 0, list));
    }

    @Test
    public void foldl() throws Exception {
        assertEquals(15, (int) Collections.foldl((x, y) -> x + y, 0, list));

        int foldlResult = (((((0) - 1) - 2) - 3) - 4) - 5;
        assertEquals(foldlResult, (int) Collections.foldl((x, y) -> x - y, 0, list));
    }
}