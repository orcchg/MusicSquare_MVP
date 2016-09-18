package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.Artist;
import com.domain.repository.ArtistRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Get list of artists from {@param artistRepository}.
 */
public class GetArtistList extends UseCase<List<Artist>> {

    final ArtistRepository artistRepository;

    /**
     * Constructs an instance of {@link GetArtistList} use case.
     *
     * Marked as {@link Inject} because this constructor will be used
     * to create an instance of the {@link GetArtistList} class to
     * provide to anybody who requests such instance.
     *
     * @param artistRepository where to get data from
     * @param threadExecutor where to push the request
     * @param postExecuteScheduler where to observe the result
     */
    @Inject
    public GetArtistList(ArtistRepository artistRepository,
                         ThreadExecutor threadExecutor, PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistRepository = artistRepository;
    }

    @Override
    protected UseCaseRunner<List<Artist>> buildUseCaseExecuteCallback() {
        return new UseCaseRunner<List<Artist>>() {
            @Override
            public List<Artist> execute() {
                return GetArtistList.this.artistRepository.artists();
            }
        };
    }
}
