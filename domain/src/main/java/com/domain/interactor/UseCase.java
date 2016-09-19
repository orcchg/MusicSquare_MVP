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
public abstract class UseCase<Result> {

    /**
     * Work-horse method to execute within this {@link UseCase}.
     */
    public interface UseCaseRunner<Result> {
        Result execute();
    }

    /**
     * Callback to notify when execution of this {@link UseCase} finishes.
     */
    public interface OnPostExecuteCallback<Result> {
        void onFinish(Result values);
        void onError(Throwable e);
    }

    private final ThreadExecutor threadExecutor;
    private final PostExecuteScheduler postExecuteScheduler;
    private OnPostExecuteCallback postExecuteCallback;

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
    public void setPostExecuteCallback(OnPostExecuteCallback postExecuteCallback) {
        this.postExecuteCallback = postExecuteCallback;
    }

    /**
     * Creates a concrete work-horse method baked into {@link UseCaseRunner}
     * which then will be executed in this {@link UseCase}.
     *
     * @return concrete work-horse method baked into {@link UseCaseRunner}.
     */
    protected abstract UseCaseRunner<Result> buildUseCaseExecuteCallback();

    /**
     * Execute this {@link UseCase} in it's {@link UseCase#threadExecutor} and
     * observe the result in it's {@link UseCase#postExecuteScheduler} via it's
     * {@link UseCase#postExecuteCallback}.
     */
    public void execute() {
        this.threadExecutor.execute(this.buildUseCaseRunnable());
    }

    /* Internal */
    // ------------------------------------------------------------------------
    private Runnable buildUseCaseRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                UseCaseRunner callback = UseCase.this.buildUseCaseExecuteCallback();
                try {
                    Result result = (Result) callback.execute();
                    UseCase.this.postExecuteScheduler.post(UseCase.this.wrapToRunnable(result));
                } catch (Throwable error) {
                    error.printStackTrace();
                    UseCase.this.postExecuteScheduler.post(UseCase.this.wrapToRunnable(error));
                }
            }
        };
    }

    private Runnable wrapToRunnable(final Result result) {
        return new Runnable() {
            @Override
            public void run() {
                UseCase.this.postExecuteCallback.onFinish(result);
            }
        };
    }

    private Runnable wrapToRunnable(final Throwable error) {
        return new Runnable() {
            @Override
            public void run() {
                UseCase.this.postExecuteCallback.onError(error);
            }
        };
    }
}
