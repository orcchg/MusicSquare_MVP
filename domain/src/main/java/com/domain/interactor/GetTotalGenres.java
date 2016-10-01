package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.TotalValue;
import com.domain.repository.IGenreRepository;

import javax.inject.Inject;

public class GetTotalGenres extends UseCase<TotalValue> {

    final IGenreRepository genreRepository;

    @Inject
    GetTotalGenres(IGenreRepository genreRepository, ThreadExecutor threadExecutor,
                   PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.genreRepository = genreRepository;
    }

    @Override
    protected TotalValue doAction() {
        return genreRepository.total();
    }
}
