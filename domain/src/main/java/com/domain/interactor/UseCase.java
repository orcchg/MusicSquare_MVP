package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * @param <Result> Generic type of result on finish of execution.
 */
public abstract class UseCase<Result> implements Runnable {

    /**
     * Callback to notify when execution of this {@link UseCase} finishes.
     */
    public interface OnPostExecuteCallback<Result> {
        void onFinish(Result values);
        void onError(Throwable e);
    }

    private final ThreadExecutor threadExecutor;
    private final PostExecuteScheduler postExecuteScheduler;
    OnPostExecuteCallback<Result> postExecuteCallback;

    /**
     * Basic construction of a {@link UseCase} class instance.
     *
     * @param threadExecutor where to push the request
     * @param postExecuteScheduler where to observe the result
     */
    protected UseCase(ThreadExecutor threadExecutor, PostExecuteScheduler postExecuteScheduler) {
        this.threadExecutor = threadExecutor;
        this.postExecuteScheduler = postExecuteScheduler;
    }

    /**
     * Sets external callback to observe the result of {@link UseCase} execution.
     *
     * @param postExecuteCallback how to process the result
     */
    public void setPostExecuteCallback(OnPostExecuteCallback<Result> postExecuteCallback) {
        this.postExecuteCallback = postExecuteCallback;
    }

    /**
     * Creates a concrete work-horse method which then will be executed in this {@link UseCase}.
     *
     * @return concrete result of execution of this {@link UseCase}.
     */
    protected abstract Result doAction();

    /**
     * Execute this {@link UseCase} in it's {@link UseCase#threadExecutor} and
     * observe the result in it's {@link UseCase#postExecuteScheduler} via it's
     * {@link UseCase#postExecuteCallback}.
     */
    public void execute() {
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            Result result = doAction();
            postExecuteScheduler.post(wrapToRunnable(result));
        } catch (Throwable error) {
            error.printStackTrace();
            postExecuteScheduler.post(wrapToRunnable(error));
        }
    }

    /* Internal */
    // ------------------------------------------------------------------------
    private Runnable wrapToRunnable(final Result result) {
        return new Runnable() {
            @Override
            public void run() {
                postExecuteCallback.onFinish(result);
            }
        };
    }

    private Runnable wrapToRunnable(final Throwable error) {
        return new Runnable() {
            @Override
            public void run() {
                postExecuteCallback.onError(error);
            }
        };
    }
}
