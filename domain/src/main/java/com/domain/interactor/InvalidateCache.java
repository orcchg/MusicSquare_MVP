package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.repository.IArtistRepository;

import javax.inject.Inject;

public class InvalidateCache extends UseCase<Boolean> {

    final IArtistRepository artistRepository;

    @Inject
    InvalidateCache(IArtistRepository artistRepository, ThreadExecutor threadExecutor,
                    PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistRepository = artistRepository;
    }

    @Override
    protected Boolean doAction() {
        return artistRepository.clear();
    }
}
