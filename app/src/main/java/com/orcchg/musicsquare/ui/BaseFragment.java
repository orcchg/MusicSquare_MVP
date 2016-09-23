package com.orcchg.musicsquare.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.orcchg.musicsquare.AndroidApplication;
import com.orcchg.musicsquare.injection.component.ApplicationComponent;
import com.orcchg.musicsquare.navigation.Navigator;

import javax.inject.Inject;

public abstract class BaseFragment<V extends MvpView, P extends MvpPresenter<V>>
        extends Fragment implements MvpView {

    protected P presenter;
    protected @Inject Navigator navigator;

    @NonNull
    protected abstract P createPresenter();

    protected abstract void injectDependencies();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        presenter = createPresenter();
        presenter.attachView((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getActivity().getApplication()).getApplicationComponent();
    }
}
