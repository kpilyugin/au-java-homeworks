package ru.spbau.mit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
        return submitTask(createTask(supplier));
    }

    @Override
    public synchronized void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private <R> Task<R> submitTask(Task<R> task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        return task;
    }

    private <R> Task<R> createTask(Supplier<R> supplier) {
        return new Task<R>() {
            @Override
            protected R computeResult() {
                return supplier.get();
            }
        };
    }

    private <R, U> Task<U> createDependentTask(Task<R> parent, Function<? super R, ? extends U> function) {
        return new Task<U>() {
            @Override
            protected U computeResult() throws LightExecutionException {
                return function.apply(parent.get());
            }
        };
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

    private abstract class Task<R> implements LightFuture<R> {
        private final List<Task<?>> waitingTasks = new ArrayList<>();

        private volatile boolean isReady = false;
        private R result;
        private LightExecutionException failCause;

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
                throw failCause;
            }
            return result;
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            Task<U> task = createDependentTask(this, f);
            if (isReady()) {
                submitTask(task);
            } else {
                synchronized (this) {
                    waitingTasks.add(task);
                }
            }
            return task;
        }

        private void execute() {
            try {
                result = computeResult();
            } catch (LightExecutionException e) {
                failCause = e;
            } catch (Exception e) {
                failCause = new LightExecutionException(e);
            }
            isReady = true;
            submitWaitingTasks();
            synchronized (this) {
                notifyAll();
            }
        }

        private synchronized void submitWaitingTasks() {
            waitingTasks.forEach(ThreadPoolImpl.this::submitTask);
            waitingTasks.clear();
        }

        protected abstract R computeResult() throws LightExecutionException;
    }
}