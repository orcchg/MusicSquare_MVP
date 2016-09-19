package com.orcchg.musicsquare.injection.module;

import android.content.Context;

import com.orcchg.musicsquare.PermissionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PermissionManagerModule {

    private final Context context;

    public PermissionManagerModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    PermissionManager providePermissionManager() {
        return new PermissionManager(this.context);
    }
}
