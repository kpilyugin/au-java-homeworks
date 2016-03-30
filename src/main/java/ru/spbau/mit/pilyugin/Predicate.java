package ru.spbau.mit.pilyugin;

public interface Predicate<A> extends Function1<A, Boolean> {

    Predicate<Object> ALWAYS_TRUE = value -> true;
    Predicate<Object> ALWAYS_FALSE = value -> false;

    default Predicate<A> and(Predicate<? super A> other) {
        return value -> apply(value) && other.apply(value);
    }

    default Predicate<A> or(Predicate<? super A> other) {
        return value -> apply(value) || other.apply(value);
    }

    default Predicate<A> not() {
        return value -> !apply(value);
    }
}
