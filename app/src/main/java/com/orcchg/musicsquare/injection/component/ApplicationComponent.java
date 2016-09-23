package com.orcchg.musicsquare.injection.component;

import android.content.Context;

import com.domain.executor.PostExecuteScheduler;
import com.domain.executor.ThreadExecutor;
import com.domain.repository.IArtistRepository;
import com.domain.repository.IGenresRepository;
import com.orcchg.data.source.remote.injection.CloudComponent;
import com.orcchg.data.source.remote.injection.CloudModule;
import com.orcchg.musicsquare.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = {ApplicationModule.class, CloudModule.class})
public interface ApplicationComponent extends CloudComponent {

    Context context();
    ThreadExecutor threadExecutor();
    PostExecuteScheduler postExecuteScheduler();
    IArtistRepository artistRepository();
    IGenresRepository genresRepository();
}
