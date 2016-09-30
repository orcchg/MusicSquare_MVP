package com.orcchg.musicsquare;

import android.app.Application;

import com.orcchg.data.source.remote.injection.CloudModule;
import com.orcchg.musicsquare.injection.component.ApplicationComponent;
import com.orcchg.musicsquare.injection.component.DaggerApplicationComponent;
import com.orcchg.musicsquare.injection.module.ApplicationModule;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class AndroidApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLogger();
        initializeInjector();
        initializeLeakDetection();
    }

    private void initializeLogger() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return getPackageName() + ":" + super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
            .cloudModule(new CloudModule(this))
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    private void initializeLeakDetection() {
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }
    }
}
