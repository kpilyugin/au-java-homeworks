package ru.spbau.mit.pilyugin;

public interface Function1<A, R> {

    R apply(A arg);

    // g(f(x))
    default <R1> Function1<A, R1> compose(Function1<? super R, ? extends R1> g) {
        return arg -> g.apply(apply(arg));
    }
}