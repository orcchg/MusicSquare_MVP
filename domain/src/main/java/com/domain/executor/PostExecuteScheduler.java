package com.domain.executor;

public interface PostExecuteScheduler {
    void post(Runnable command);
}
