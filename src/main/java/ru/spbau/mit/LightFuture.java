package ru.spbau.mit;

import java.util.function.Function;

public interface LightFuture<R> {
    boolean isReady();

    /**
     * Returns immediately if task is ready, or waits until it's done
     * @return future result
     * @throws LightExecutionException if exception was thrown during task
     * execution
     */
    R get() throws LightExecutionException;

    <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f);
}