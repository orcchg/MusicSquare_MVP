package com.domain.executor;

import com.domain.interactor.UseCase;

import javax.inject.Inject;

public class UseCaseExecutor extends ThreadExecutor {

    @Inject
    public UseCaseExecutor() {
    }

    public <Result> void execute(UseCase<Result> useCase) {
        super.execute(useCase);
    }
}
