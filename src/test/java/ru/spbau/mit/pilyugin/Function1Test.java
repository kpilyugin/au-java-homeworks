package ru.spbau.mit.pilyugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {

    @Test
    public void apply() {
        Function1<Integer, Integer> f = value -> value * value;
        assertEquals(25, (int) f.apply(5));
        assertNotEquals(5, (int) f.apply(2));
    }

    @Test
    public void compose() {
        Function1<Integer, Integer> f = (value) -> value * value;
        Function1<Number, Integer> g = (value) -> value.intValue() * 2;

        assertEquals(50, (int) f.compose(g).apply(5));
    }
}