package ru.spbau.mit.pilyugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateTest {

    private static final Predicate<Integer> POSITIVE = value -> value > 0;
    private static final Predicate<Number> EVEN = value -> value.intValue() % 2 == 0;

    private static final Predicate<Object> CHECK_NOT_CALLED = value -> {
        throw new RuntimeException("Not lazy evaluation");
    };

    @Test
    public void and() {
        assertTrue(POSITIVE.and(EVEN).apply(4));
        assertFalse(POSITIVE.and(EVEN).apply(5));
    }

    @Test
    public void or() {
        assertTrue(POSITIVE.or(EVEN).apply(5));
        assertTrue(POSITIVE.or(EVEN).apply(-2));
        assertFalse(POSITIVE.or(EVEN).apply(-1));
    }

    @Test
    public void lazy() {
        assertTrue(EVEN.or(CHECK_NOT_CALLED).apply(0));
        assertFalse(EVEN.and(CHECK_NOT_CALLED).apply(1));
    }

    @Test
    public void not() {
        assertTrue(EVEN.not().apply(3));
        assertFalse(EVEN.not().apply(4));
    }

    @Test
    public void always() {
        assertTrue(Predicate.ALWAYS_TRUE.apply(42));
        assertFalse(Predicate.ALWAYS_FALSE.apply("hi!"));
        assertTrue(Predicate.ALWAYS_TRUE.or(Predicate.ALWAYS_FALSE).apply(5));
    }
}