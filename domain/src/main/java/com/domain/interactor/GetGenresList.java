package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.Genre;
import com.domain.repository.IGenresRepository;

import java.util.List;

import javax.inject.Inject;

public class GetGenresList extends UseCase<List<Genre>> {

    final IGenresRepository genresRepository;

    @Inject
    GetGenresList(IGenresRepository genresRepository, ThreadExecutor threadExecutor,
                  PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.genresRepository = genresRepository;
    }

    @Override
    protected UseCaseRunner<List<Genre>> buildUseCaseExecuteCallback() {
        return new UseCaseRunner<List<Genre>>() {
            @Override
            public List<Genre> execute() {
                return GetGenresList.this.genresRepository.genres();
            }
        };
    }
}
