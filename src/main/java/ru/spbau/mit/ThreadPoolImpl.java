package ru.spbau.mit;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private final Thread[] threads;
    private final Queue<Task<?>> tasks = new LinkedList<>();

    public ThreadPoolImpl(int numThreads) {
        threads = new Thread[numThreads];
        Runnable worker = new Worker();
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(worker);
            threads[i].start();
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        Task<R> task = new Task<>(supplier);
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        return task;
    }

    @Override
    public synchronized void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Task<?> task;
                    synchronized (tasks) {
                        while (tasks.isEmpty()) {
                            tasks.wait();
                        }
                        task = tasks.remove();
                    }
                    task.execute();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class Task<R> implements LightFuture<R> {
        private final Supplier<R> supplier;

        private volatile boolean isReady = false;
        private R result;
        private Exception failCause;

        public Task(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public R get() throws LightExecutionException {
            if (!isReady) {
                synchronized (this) {
                    while (!isReady) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            if (failCause != null) {
                throw new LightExecutionException(failCause);
            }
            return result;
        }

        public void execute() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                failCause = e;
            }
            isReady = true;
            synchronized (this) {
                notifyAll();
            }
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            return submit(() -> {
                try {
                    R result = get();
                    return f.apply(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}