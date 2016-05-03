package ru.spbau.mit;

import java.util.function.Supplier;

public interface ThreadPool {
    <R> LightFuture<R> submit(Supplier<R> supplier);
    void shutdown();
}