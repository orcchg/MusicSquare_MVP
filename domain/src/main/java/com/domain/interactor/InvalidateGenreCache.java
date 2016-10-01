package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.repository.IGenreRepository;

import javax.inject.Inject;

public class InvalidateGenreCache extends UseCase<Boolean> {

    final IGenreRepository genreRepository;

    @Inject
    InvalidateGenreCache(IGenreRepository genreRepository, ThreadExecutor threadExecutor,
                         PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.genreRepository = genreRepository;
    }

    @Override
    protected Boolean doAction() {
        return genreRepository.clear();
    }
}
