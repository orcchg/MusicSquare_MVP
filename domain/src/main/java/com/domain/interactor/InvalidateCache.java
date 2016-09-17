package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.repository.ArtistRepository;

import javax.inject.Inject;

public class InvalidateCache extends UseCase<Boolean> {

    private final ArtistRepository artistRepository;

    @Inject
    public InvalidateCache(ArtistRepository artistRepository, ThreadExecutor threadExecutor,
                           PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistRepository = artistRepository;
    }

    @Override
    protected UseCaseRunner<Boolean> buildUseCaseExecuteCallback() {
        return new UseCaseRunner<Boolean>() {
            @Override
            public Boolean execute() {
                return InvalidateCache.this.artistRepository.clear();
            }
        };
    }
}
