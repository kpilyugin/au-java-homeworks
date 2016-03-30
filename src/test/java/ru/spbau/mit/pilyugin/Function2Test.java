package ru.spbau.mit.pilyugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    private static final Function2<Integer, Double, Double> MULTIPLY = (x, y) -> x * y;

    @Test
    public void apply() {
        assertEquals(18., MULTIPLY.apply(2, 9.), 0);
    }

    @Test
    public void compose() {
        Function1<Number, Double> numberSqrt = num -> Math.sqrt(num.doubleValue());
        assertEquals(5., MULTIPLY.compose(numberSqrt).apply(5, 5.), 0);
    }

    @Test
    public void bind1() {
        Function1<Double, Double> multiply2 = MULTIPLY.bind1(2);
        assertEquals(4., multiply2.apply(2.), 0);
        assertEquals(6., multiply2.apply(3.), 0);
    }

    @Test
    public void bind2() {
        Function2<Integer, Integer, Integer> subtract = (x, y) -> x - y;
        Function1<Integer, Integer> subtract3 = subtract.bind2(3);
        assertEquals(7 - 3, (int) subtract3.apply(7));
    }

    @Test
    public void curry() {
        assertEquals(MULTIPLY.curry().apply(2).apply(5.), MULTIPLY.apply(2, 5.), 0);
        assertEquals(MULTIPLY.curry().apply(0).apply(100500.), MULTIPLY.apply(0, 100500.), 0);
    }
}