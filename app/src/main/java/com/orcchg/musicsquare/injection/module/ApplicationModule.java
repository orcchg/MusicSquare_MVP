package com.orcchg.musicsquare.injection.module;

import android.content.Context;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.executor.UseCaseExecutor;
import com.domain.repository.ArtistRepository;
import com.orcchg.data.source.remote.artist.DataSource;
import com.orcchg.data.source.local.artist.DatabaseSourceImpl;
import com.orcchg.data.source.local.FileManager;
import com.orcchg.data.source.local.artist.LocalSource;
import com.orcchg.data.source.remote.artist.server.ServerArtistRepositoryImpl;
import com.orcchg.data.source.remote.artist.server.ServerCloudSource;
import com.orcchg.data.source.remote.artist.yandex.YandexCloudSource;
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

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application.getApplicationContext();
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(UseCaseExecutor executor) {
        return executor;
    }

    @Provides
    @Singleton
    PostExecuteScheduler providePostExecuteScheduler(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    @Named("yandexCloud")
    DataSource provideYandexDataSource(YandexCloudSource dataSource) {
        return dataSource;
    }

    @Provides
    @Singleton
    @Named("serverCloud")
    DataSource provideServerDataSource(ServerCloudSource dataSource) {
        return dataSource;
    }

    @Provides
    @Singleton
    LocalSource provideLocalDataSource(FileManager fileManager) {
        return new DatabaseSourceImpl(this.application.getApplicationContext(), fileManager);
    }

    @Provides
    @Singleton
    ArtistRepository provideArtistRepository(ServerArtistRepositoryImpl repository) {
        return repository;
    }
}
