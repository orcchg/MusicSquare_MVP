package com.orcchg.musicsquare.injection.module;

import com.orcchg.musicsquare.navigation.Navigator;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigationModule {

    @Provides
    Navigator provideNavigator() {
        return new Navigator();
    }
}
