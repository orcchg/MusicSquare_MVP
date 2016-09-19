package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.Artist;
import com.domain.repository.IArtistRepository;

import javax.inject.Inject;

public class GetArtistDetails extends UseCase<Artist> {

    final long artistId;
    final IArtistRepository artistRepository;

    /**
     * Constructs an instance of {@link GetArtistDetails} use case.
     *
     * Marked as {@link Inject} because this constructor will be used
     * to create an instance of the {@link GetArtistDetails} class to
     * provide to anybody who requests such instance.
     *
     * @param artistId id of specific {@link Artist} in {@param artistRepository}
     * @param artistRepository where to get data from
     * @param threadExecutor where to push the request
     * @param postExecuteScheduler where to observe the result
     */
    @Inject
    public GetArtistDetails(long artistId, IArtistRepository artistRepository,
                            ThreadExecutor threadExecutor, PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistId = artistId;
        this.artistRepository = artistRepository;
    }

    @Override
    protected UseCaseRunner<Artist> buildUseCaseExecuteCallback() {
        return new UseCaseRunner<Artist>() {
            @Override
            public Artist execute() {
                return GetArtistDetails.this.artistRepository.artist(GetArtistDetails.this.artistId);
            }
        };
    }
}
