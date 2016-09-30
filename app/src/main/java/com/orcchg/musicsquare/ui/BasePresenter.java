package com.orcchg.musicsquare.ui;

import android.support.annotation.Nullable;

import com.orcchg.musicsquare.navigation.Navigator;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> viewRef;
    protected @Inject Navigator navigator;

    @Override
    public void attachView(V view) {
        viewRef = new WeakReference<>(view);
    }

    @Nullable
    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    @Override
    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    @Override
    public void onCreate() {
        // to override
    }

    @Override
    public void onStart() {
        // to override
    }

    @Override
    public void onResume() {
    // to override
    }

    @Override
    public void onPause() {
    // to override
    }

    @Override
    public void onStop() {
        // to override
    }

    @Override
    public void onDestroy() {
        // to override
    }
}
