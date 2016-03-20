package ru.spbau.mit.pilyugin;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;

public class CollectionsTest {

    private final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

    @Test
    public void map() throws Exception {
        List<Integer> result = Collections.map(list, x -> x * x);
        List<Integer> expected = Arrays.asList(1, 4, 9, 16, 25);
        assertEquals(expected, result);
    }

    @Test
    public void filter() throws Exception {
        List<Integer> result = Collections.filter(list, x -> x % 2 == 0);
        List<Integer> expected = Arrays.asList(2, 4);
        assertEquals(expected, result);
    }

    @Test
    public void takeWhile() throws Exception {
        List<Integer> result = Collections.takeWhile(list, x -> x < 3);
        List<Integer> expected = Arrays.asList(1, 2);
        assertEquals(expected, result);
    }

    @Test
    public void takeUnless() throws Exception {
        List<Integer> result = Collections.takeUnless(list, x -> x > 3);
        List<Integer> expected = Arrays.asList(1, 2, 3);
        assertEquals(expected, result);
    }

    @Test
    public void foldrEmpty() throws Exception {
        Object initial = new Object();
        assertEquals(initial, Collections.foldr(EMPTY_LIST, null, initial));
    }

    @Test
    public void foldr() throws Exception {
        assertEquals(15, (int) Collections.foldr(list, (x, y) -> x + y, 0));

        int foldrResult = (1 - (2 - (3 - (4 - (5)))));
        assertEquals(foldrResult, (int) Collections.foldr(list, (x, y) -> x - y, 0));
    }

    @Test
    public void foldl() throws Exception {
        assertEquals(15, (int) Collections.foldl(list, (x, y) -> x + y, 0));

        int foldlResult = (((((0) - 1) - 2) - 3) - 4) - 5;
        assertEquals(foldlResult, (int) Collections.foldl(list, (x, y) -> x - y, 0));
    }
}