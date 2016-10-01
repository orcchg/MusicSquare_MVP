package com.domain.interactor;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.model.TotalValue;
import com.domain.repository.IArtistRepository;

import java.util.List;

import javax.inject.Inject;

public class GetTotalArtists extends UseCase<TotalValue> {

    public static class Parameters {
        List<String> genres;

        Parameters(Builder builder) {
            this.genres = builder.genres;
        }

        public static class Builder {
            List<String> genres;

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

    @Inject
    GetTotalArtists(IArtistRepository artistRepository, ThreadExecutor threadExecutor,
                    PostExecuteScheduler postExecuteScheduler) {
        super(threadExecutor, postExecuteScheduler);
        this.artistRepository = artistRepository;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    protected TotalValue doAction() {
        List<String> genres = parameters.genres;
        return artistRepository.total(genres);
    }
}
