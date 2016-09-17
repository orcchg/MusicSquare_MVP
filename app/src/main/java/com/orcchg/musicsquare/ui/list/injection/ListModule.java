package com.orcchg.musicsquare.ui.list.injection;

import com.domain.interactor.GetArtistList;
import com.orcchg.musicsquare.injection.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides methods to instantiate {@link GetArtistList}.
 */
@Module
public class ListModule {

    /**
     * Constructor to configure and then instantiate the {@link ListModule} class.
     * It will be invoked when a dagger-generated implementation of {@link ListComponent}
     * will have been built.
     */
    public ListModule() {
    }

    /**
     * Specifies how to actually construct an injectable instance of {@link GetArtistList} class.
     *
     * Since {@link GetArtistList} class has an inject-constructor, there is no need to call it
     * to instantiate the class, because Dagger will handle it by itself.
     *
     * @param getArtistList parameter to configure an instance of {@link GetArtistList} class
     *                      before it will be actually injected by {@link ListComponent} implementation.
     * @return an instance to inject
     */
//    @Provides
//    @PerActivity
//    GetArtistList provideGetArtistList(GetArtistList getArtistList) {
//        return getArtistList;
//    }
}
