package ru.spbau.mit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private final Thread[] threads;
    private final Queue<AbstractTask<?>> tasks = new LinkedList<>();

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
        return submitTask(new Task<>(supplier));
    }

    @Override
    public synchronized void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private <R> AbstractTask<R> submitTask(AbstractTask<R> task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        return task;
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    AbstractTask<?> task;
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

    private abstract class AbstractTask<R> implements LightFuture<R> {
        private final List<DependentTask<R, ?>> waitingTasks = new ArrayList<>();

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
            DependentTask<R, U> task = new DependentTask<>(this, f);
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

    private class Task<R> extends AbstractTask<R> {
        private final Supplier<R> supplier;

        public Task(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected R computeResult() {
            return supplier.get();
        }
    }

    private class DependentTask<R, U> extends AbstractTask<U> {
        private final Function<? super R, ? extends U> function;
        private final AbstractTask<R> parent;

        DependentTask(AbstractTask<R> parent, Function<? super R, ? extends U> function) {
            this.parent = parent;
            this.function = function;
        }

        @Override
        protected U computeResult() throws LightExecutionException {
            return function.apply(parent.get());
        }
    }
}