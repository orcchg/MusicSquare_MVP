package com.orcchg.musicsquare.executor;

import com.domain.executor.PostExecuteScheduler;

import javax.inject.Inject;

public class ThisThread implements PostExecuteScheduler {

    @Inject
    ThisThread() {
    }

    @Override
    public void post(Runnable command) {
        command.run();
    }
}
