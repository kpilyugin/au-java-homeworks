package ru.spbau.mit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ThreadPoolTest {

    private static final int NUM_THREADS = 8;

    private ThreadPool threadPool;

    @Before
    public void setUp() {
        threadPool = new ThreadPoolImpl(NUM_THREADS);
    }

    @After
    public void tearDown() {
        threadPool.shutdown();
    }

    @Test
    public void testSimpleTask() throws LightExecutionException {
        LightFuture<Integer> future = threadPool.submit(() -> {
            delayExecution(10);
            return IntStream.range(1, 5).reduce((a, b) -> a * b).orElse(0);
        });
        assertFalse(future.isReady());
        assertEquals(24, (int) future.get());
        assertTrue(future.isReady());
    }

    @Test(expected = LightExecutionException.class)
    public void testFailure() throws LightExecutionException {
        LightFuture<Void> failingTask = threadPool.submit(() -> {
            throw new RuntimeException();
        });
        failingTask.get();
    }

    @Test
    public void testAndThen() throws LightExecutionException {
        LightFuture<Integer> first = threadPool.submit(() -> {
            delayExecution(10);
            return 1;
        });
        LightFuture<Integer> second = first.thenApply(a -> {
            delayExecution(10);
            return a * 2;
        });
        LightFuture<Integer> third = second.thenApply(a -> a * 3);

        assertFalse(first.isReady());
        assertFalse(second.isReady());
        assertFalse(third.isReady());

        assertEquals(1, (int) first.get());
        assertTrue(first.isReady());
        assertFalse(second.isReady());
        assertFalse(third.isReady());

        assertEquals(6, (int) third.get());
        assertTrue(first.isReady());
        assertTrue(second.isReady());
        assertTrue(third.isReady());
    }

    @Test(expected = LightExecutionException.class)
    public void testAndThenFailure() throws LightExecutionException {
        LightFuture<Integer> failingTask = threadPool.submit(() -> {
            throw new RuntimeException();
        });
        LightFuture<Integer> next = failingTask.thenApply(Function.identity());
        next.get();
    }

    @Test
    public void testHeavy() throws LightExecutionException {
        List<LightFuture<Integer>> futures = new ArrayList<>();
        int numTasks = 100000;
        int rangeSize = 10000;
        for (int i = 0; i < numTasks; i++) {
            futures.add(threadPool.submit(() -> IntStream.range(0, rangeSize).sum()));
        }
        for (LightFuture<Integer> future : futures) {
            assertEquals(rangeSize * (rangeSize - 1) / 2, (int) future.get());
        }
    }

    @Test
    public void testLongWaitingTask() throws LightExecutionException {
        for (int i = 0; i < NUM_THREADS - 1; i++) {
            LightFuture<Void> waiting = threadPool.submit(() -> {
                delayExecution(10000);
                return null;
            });
            // all threads except one are waiting, add task depending on waiting task
            waiting.thenApply(Function.identity());
        }
        LightFuture<Integer> task = threadPool.submit(() -> 3);
        assertEquals(3, (int) task.get());
        threadPool.shutdown();
    }

    @Test
    public void testThreadPoolSize() throws LightExecutionException {
        Set<Long> ids = new HashSet<>();
        List<LightFuture<Long>> futures = new ArrayList<>();

        int numTasks = 50;
        for (int i = 0; i < numTasks; i++) {
            futures.add(threadPool.submit(() -> {
                delayExecution(10);
                return Thread.currentThread().getId();
            }));
        }
        for (LightFuture<Long> future : futures) {
            ids.add(future.get());
        }
        assertEquals(NUM_THREADS, ids.size());
    }

    private static void delayExecution(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}