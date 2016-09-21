package com.orcchg.musicsquare.executor;

import android.os.Handler;
import android.os.Looper;

import com.domain.executor.PostExecuteScheduler;

import javax.inject.Inject;

public class UIThread implements PostExecuteScheduler {

    private static final Handler UI_LOOP_HANDLER = new Handler(Looper.getMainLooper());

    @Inject
    UIThread() {}

    @Override
    public void post(Runnable command) {
        UI_LOOP_HANDLER.post(command);
    }
}
