package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.Artist;
import com.domain.repository.IArtistRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Get list of artists from {@param artistRepository}.
 */
public class GetArtistList extends UseCase<List<Artist>> {

    public static class Parameters {
        int limit = -1;
        int offset = 0;
        List<String> genres;

        Parameters(Builder builder) {
            this.limit = builder.limit;
            this.offset = builder.offset;
            this.genres = builder.genres;
        }

        public static class Builder {
            int limit = -1;
            int offset = 0;
            List<String> genres;

            public Builder setLimit(int limit) {
                this.limit = limit;
                return this;
            }

            public Builder setOffset(int offset) {
                this.offset = offset;
                return this;
            }

            public Builder setGenres(List<String> genres) {
                this.genres = genres;
                return this;
            }

            public Parameters build() {
                return new Parameters(this);
            }
        }
    }

    final IArtistRepository artistRepository;
    Parameters parameters;

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
    GetArtistList(IArtistRepository artistRepository, ThreadExecutor threadExecutor,
                  PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistRepository = artistRepository;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    protected List<Artist> doAction() {
        int limit = GetArtistList.this.parameters.limit;
        int offset = GetArtistList.this.parameters.offset;
        List<String> genres = GetArtistList.this.parameters.genres;
        return GetArtistList.this.artistRepository.artists(limit, offset, genres);
    }
}
