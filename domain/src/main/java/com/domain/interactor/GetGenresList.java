package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.repository.IGenresRepository;

import java.util.List;

import javax.inject.Inject;

public class GetGenresList extends UseCase<List<String>> {

    final IGenresRepository genresRepository;

    @Inject
    GetGenresList(IGenresRepository genresRepository, ThreadExecutor threadExecutor,
                  PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.genresRepository = genresRepository;
    }

    @Override
    protected UseCaseRunner<List<String>> buildUseCaseExecuteCallback() {
        return new UseCaseRunner<List<String>>() {
            @Override
            public List<String> execute() {
                return GetGenresList.this.genresRepository.genres();
            }
        };
    }
}
