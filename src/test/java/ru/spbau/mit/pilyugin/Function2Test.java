package ru.spbau.mit.pilyugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    private final Function2<Integer, Double, Double> multiply = (x, y) -> x * y;

    @Test
    public void apply() throws Exception {
        assertEquals(18., multiply.apply(2, 9.), 0);
    }

    @Test
    public void compose() throws Exception {
        assertEquals(5., multiply.compose(Math::sqrt).apply(5, 5.), 0);
    }

    @Test
    public void bind1() throws Exception {
        Function1<Double, Double> multiply2 = multiply.bind1(2);
        assertEquals(4., multiply2.apply(2.), 0);
        assertEquals(6., multiply2.apply(3.), 0);
    }

    @Test
    public void bind2() throws Exception {
        Function2<Integer, Integer, Integer> subtract = (x, y) -> x - y;
        Function1<Integer, Integer> subtract3 = subtract.bind2(3);
        assertEquals(7 - 3, (int) subtract3.apply(7));
    }

    @Test
    public void curry() throws Exception {
        assertEquals(multiply.curry().apply(2).apply(5.), multiply.apply(2, 5.), 0);
        assertEquals(multiply.curry().apply(0).apply(100500.), multiply.apply(0, 100500.), 0);
    }
}