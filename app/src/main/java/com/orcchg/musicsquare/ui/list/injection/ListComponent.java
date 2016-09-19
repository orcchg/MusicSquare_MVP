package com.orcchg.musicsquare.ui.list.injection;

import com.orcchg.musicsquare.injection.PerActivity;
import com.orcchg.musicsquare.injection.component.ApplicationComponent;
import com.orcchg.musicsquare.ui.list.ListActivity;
import com.orcchg.musicsquare.ui.list.ListPresenter;

import dagger.Component;

@PerActivity
@Component(modules = {ListModule.class}, dependencies = {ApplicationComponent.class})
public interface ListComponent {

    /**
     * A member-injection method.
     *
     * Injects all fields marked with {@link javax.inject.Inject} annotation
     * into {@link ListActivity} specified by {@param activity} parameter.
     *
     * @param activity where to inject fields
     */
    void inject(ListActivity activity);

    /**
     * Template for dagger-generated factory method to provide
     * an instance of {@link ListPresenter} class for where this
     * {@link ListComponent} injects to.
     */
    ListPresenter presenter();
}
