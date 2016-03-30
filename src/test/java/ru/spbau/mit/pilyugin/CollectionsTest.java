package ru.spbau.mit.pilyugin;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class CollectionsTest {

    private static final List<Integer> LIST = Arrays.asList(1, 2, 3, 4, 5);

    @Test
    public void map() {
        Function1<Number, String> sqrToString = x -> String.valueOf(x.intValue() * x.intValue());
        List<String> result = Collections.map(LIST, sqrToString);
        List<String> expected = Arrays.asList("1", "4", "9", "16", "25");
        assertEquals(expected, result);
    }

    @Test
    public void filter() {
        assertEquals(LIST, Collections.filter(LIST, Predicate.ALWAYS_TRUE));
        assertEquals(emptyList(), Collections.filter(LIST, Predicate.ALWAYS_FALSE));

        Predicate<Number> even = x -> x.intValue() % 2 == 0;
        List<Integer> result = Collections.filter(LIST, even);
        List<Integer> expected = Arrays.asList(2, 4);
        assertEquals(expected, result);
    }

    @Test
    public void takeWhile() {
        Predicate<Number> lessThanThree = x -> x.intValue() < 3;
        List<Integer> result = Collections.takeWhile(LIST, lessThanThree);
        List<Integer> expected = Arrays.asList(1, 2);
        assertEquals(expected, result);
    }

    @Test
    public void takeUnless() {
        Predicate<Number> greaterThanThree = x -> x.intValue() > 3;
        List<Integer> result = Collections.takeUnless(LIST, greaterThanThree);
        List<Integer> expected = Arrays.asList(1, 2, 3);
        assertEquals(expected, result);
    }

    @Test
    public void foldEmpty() {
        Object initial = new Object();
        assertEquals(initial, Collections.foldr(emptyList(), null, initial));
        assertEquals(initial, Collections.foldl(emptyList(), null, initial));
    }

    @Test
    public void foldSum() {
        Function2<Number, Number, Double> sumDouble =  (x, y) -> x.doubleValue() + y.doubleValue();
        assertEquals(15, Collections.foldr(LIST, sumDouble, 0.), 0);
        assertEquals(15, Collections.foldl(LIST, sumDouble, 0.), 0);
    }

    @Test
    public void foldDiff() {
        int foldrResult = (1 - (2 - (3 - (4 - (5)))));
        assertEquals(foldrResult, (int) Collections.foldr(LIST, (x, y) -> x - y, 0));

        int foldlResult = (((((0) - 1) - 2) - 3) - 4) - 5;
        assertEquals(foldlResult, (int) Collections.foldl(LIST, (x, y) -> x - y, 0));
    }
}