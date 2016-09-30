package com.orcchg.musicsquare.ui.details.injection;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.interactor.GetArtistDetails;
import com.domain.repository.IArtistRepository;
import com.orcchg.musicsquare.injection.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides methods to instantiate {@link GetArtistDetails}.
 */
@Module
public class DetailsModule {

    private final long artistId;

    /**
     * Constructor to configure and then instantiate the {@link DetailsModule} class.
     * It will be invoked when a dagger-generated implementation of {@link DetailsComponent}
     * will have been built.
     *
     * Sets a {@link DetailsModule#artistId} to create specific {@link GetArtistDetails} on demand.
     */
    public DetailsModule(long artistId) {
        this.artistId = artistId;
    }

    /**
     * Specifies how to actually construct an injectable instance of {@link GetArtistDetails} class.
     *
     * Despite {@link GetArtistDetails} class has an inject-constructor, we need to call it
     * to instantiate the class with {@link GetArtistDetails#artistId} specified, because the object
     * is configurable.
     *
     * @return an instance to inject
     */
    @Provides
    @PerActivity
    GetArtistDetails provideGetArtistDetails(IArtistRepository artistRepository,
            ThreadExecutor threadExecutor, PostExecuteScheduler postExecuteScheduler) {
        return new GetArtistDetails(artistId, artistRepository, threadExecutor, postExecuteScheduler);
    }
}
