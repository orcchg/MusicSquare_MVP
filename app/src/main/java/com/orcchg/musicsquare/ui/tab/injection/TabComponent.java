package com.orcchg.musicsquare.ui.tab.injection;

import com.orcchg.musicsquare.injection.PerActivity;
import com.orcchg.musicsquare.injection.component.ApplicationComponent;
import com.orcchg.musicsquare.ui.tab.TabActivity;
import com.orcchg.musicsquare.ui.tab.TabPresenter;

import dagger.Component;

@PerActivity
@Component(modules = {TabModule.class}, dependencies = {ApplicationComponent.class})
public interface TabComponent {

    void inject(TabActivity activity);

    TabPresenter presenter();
}
