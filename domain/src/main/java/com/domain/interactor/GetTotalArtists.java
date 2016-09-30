package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.TotalValue;
import com.domain.repository.IArtistRepository;

import javax.inject.Inject;

public class GetTotalArtists extends UseCase<TotalValue> {

    final IArtistRepository artistRepository;

    @Inject
    GetTotalArtists(IArtistRepository artistRepository, ThreadExecutor threadExecutor,
                    PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistRepository = artistRepository;
    }

    @Override
    protected TotalValue doAction() {
        return artistRepository.total();
    }
}
