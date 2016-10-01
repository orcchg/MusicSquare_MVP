package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.Genre;
import com.domain.repository.IGenreRepository;

import java.util.List;

import javax.inject.Inject;

public class GetGenresList extends UseCase<List<Genre>> {

    final IGenreRepository genresRepository;

    @Inject
    GetGenresList(IGenreRepository genresRepository, ThreadExecutor threadExecutor,
                  PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.genresRepository = genresRepository;
    }

    @Override
    protected List<Genre> doAction() {
        return genresRepository.genres();
    }
}
