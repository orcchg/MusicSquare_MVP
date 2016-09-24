package com.orcchg.musicsquare.injection.module;

import android.content.Context;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.executor.UseCaseExecutor;
import com.domain.repository.IArtistRepository;
import com.domain.repository.IGenresRepository;
import com.orcchg.data.source.local.DatabaseHelper;
import com.orcchg.data.source.local.artist.ArtistLocalSource;
import com.orcchg.data.source.local.artist.ArtistLocalSourceImpl;
import com.orcchg.data.source.remote.artist.ArtistDataSource;
import com.orcchg.data.source.remote.artist.GenresDataSource;
import com.orcchg.data.source.remote.artist.server.ServerCloudSource;
import com.orcchg.data.source.remote.artist.yandex.YandexCloudSource;
import com.orcchg.data.source.repository.artist.ServerArtistRepositoryImpl;
import com.orcchg.data.source.repository.artist.ServerGenresRepositoryImpl;
import com.orcchg.musicsquare.AndroidApplication;
import com.orcchg.musicsquare.executor.UIThread;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {

    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides @Singleton
    Context provideApplicationContext() {
        return this.application.getApplicationContext();
    }

    @Provides @Singleton
    ThreadExecutor provideThreadExecutor(UseCaseExecutor executor) {
        return executor;
    }

    @Provides @Singleton
    PostExecuteScheduler providePostExecuteScheduler(UIThread uiThread) {
        return uiThread;
    }

    @Provides @Singleton @Named("yandexCloud")
    ArtistDataSource provideYandexDataSource(YandexCloudSource dataSource) {
        return dataSource;
    }

    @Provides @Singleton @Named("serverCloud")
    ArtistDataSource provideServerDataSource(ServerCloudSource dataSource) {
        return dataSource;
    }

    @Provides @Singleton
    GenresDataSource provideGenresDataSource(ServerCloudSource dataSource) {
        return dataSource;
    }

    @Provides @Singleton
    ArtistLocalSource provideLocalDataSource(DatabaseHelper databaseHelper) {
        return new ArtistLocalSourceImpl(databaseHelper);
    }

    @Provides @Singleton
    IArtistRepository provideArtistRepository(ServerArtistRepositoryImpl repository) {
        return repository;
    }

    @Provides @Singleton
    IGenresRepository provideGenresRepository(ServerGenresRepositoryImpl repository) {
        return repository;
    }
}
