package com.domain.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ThreadExecutor {
    protected static final int INITIAL_POOL_SIZE = 3;
    protected static final int MAX_POOL_SIZE = 5;
    protected static final int KEEP_ALIVE_TIME = 10;
    protected static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    protected final BlockingQueue<Runnable> workQueue;
    protected final ThreadFactory threadFactory;
    protected final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public ThreadExecutor() {
        this.workQueue = new LinkedBlockingQueue<>();
        this.threadFactory = new JobThreadFactory();
        this.threadPoolExecutor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, this.workQueue, this.threadFactory);
    }

    public void execute(Runnable command) {
        if (command == null) {
            throw new IllegalArgumentException("Runnable to execute cannot be null");
        }
        this.threadPoolExecutor.execute(command);
    }

    protected static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "musicsquare_thread_";
        private int counter = 0;

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, THREAD_NAME + counter++);
        }
    }
}
