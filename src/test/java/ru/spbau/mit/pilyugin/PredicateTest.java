package ru.spbau.mit.pilyugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateTest {

    private final Predicate<Integer> positive = value -> value > 0;
    private final Predicate<Integer> even = value -> value % 2 == 0;

    @Test
    public void and() throws Exception {
        assertTrue(positive.and(even).apply(4));
        assertFalse(positive.and(even).apply(5));
    }

    @Test
    public void or() throws Exception {
        assertTrue(positive.or(even).apply(5));
        assertTrue(positive.or(even).apply(-2));
        assertFalse(positive.or(even).apply(-1));
    }

    @Test
    public void lazy() throws Exception {
        Predicate<Integer> shouldNotBeCalled = value -> {
            throw new RuntimeException("not lazy evaluation");
        };
        assertTrue(even.or(shouldNotBeCalled).apply(0));
    }

    @Test
    public void not() throws Exception {
        assertTrue(Predicate.not(even).apply(3));
        assertFalse(Predicate.not(even).apply(4));
    }

    @Test
    public void always() {
        assertTrue(Predicate.ALWAYS_TRUE.apply(42));
        assertFalse(Predicate.ALWAYS_FALSE.apply("hi!"));
        assertTrue(Predicate.ALWAYS_TRUE.or(Predicate.ALWAYS_FALSE).apply(5));
    }
}