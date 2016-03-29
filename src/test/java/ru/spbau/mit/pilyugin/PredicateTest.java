package ru.spbau.mit.pilyugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateTest {

    private static final Predicate<Integer> POSITIVE = value -> value > 0;
    private static final Predicate<Integer> EVEN = value -> value % 2 == 0;

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
    public void lazyAnd() {
        Predicate<Integer> shouldNotBeCalled = value -> {
            throw new RuntimeException("not lazy evaluation");
        };
        assertFalse(EVEN.and(shouldNotBeCalled).apply(1));
    }

    @Test
    public void lazyOr() {
        Predicate<Integer> shouldNotBeCalled = value -> {
            throw new RuntimeException("not lazy evaluation");
        };
        assertTrue(EVEN.or(shouldNotBeCalled).apply(0));
    }

    @Test
    public void wildcardAnd() {
        Predicate<Number> base = value -> value.intValue() == 0;
        Predicate<Integer> derived = value -> value == 0;
        assertTrue(derived.and(base).apply(0));
    }

    @Test
    public void wildcardOr() {
        Predicate<Number> base = value -> value.intValue() == 0;
        Predicate<Integer> derived = value -> value == 5;
        assertTrue(derived.or(base).apply(0));
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