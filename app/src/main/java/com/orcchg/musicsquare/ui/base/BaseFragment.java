package com.orcchg.musicsquare.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.orcchg.musicsquare.AndroidApplication;
import com.orcchg.musicsquare.injection.component.ApplicationComponent;

import hugo.weaving.DebugLog;

public abstract class BaseFragment<V extends MvpView, P extends MvpPresenter<V>>
        extends Fragment implements MvpView {

    protected P presenter;

    private boolean isStateRestored = false;

    @NonNull
    protected abstract P createPresenter();

    protected abstract void injectDependencies();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStateRestored = savedInstanceState != null;
        injectDependencies();
        presenter = createPresenter();
        presenter.attachView((V) this);
        presenter.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter.detachView();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getActivity().getApplication()).getApplicationComponent();
    }

    @DebugLog
    protected boolean isStateRestored() {
        return isStateRestored;
    }
}
