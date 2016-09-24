package com.orcchg.musicsquare.injection.component;

import com.orcchg.musicsquare.injection.PerActivity;
import com.orcchg.musicsquare.injection.module.NavigationModule;
import com.orcchg.musicsquare.navigation.Navigator;
import com.orcchg.musicsquare.ui.list.ListActivity;

import dagger.Component;

@PerActivity
@Component(modules = {NavigationModule.class})
public interface NavigationComponent {

    void inject(ListActivity activity);

    Navigator navigator();
}
