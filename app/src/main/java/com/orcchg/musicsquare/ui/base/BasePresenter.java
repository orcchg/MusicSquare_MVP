package com.orcchg.musicsquare.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.orcchg.musicsquare.navigation.Navigator;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import hugo.weaving.DebugLog;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> viewRef;
    protected @Inject Navigator navigator;

    private boolean isStateRestored = false;

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

    @DebugLog
    protected boolean isStateRestored() {
        return isStateRestored;
    }

    @Override
    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    @DebugLog @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        isStateRestored = savedInstanceState != null;
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

    @DebugLog @Override
    public void onSaveInstanceState(Bundle outState) {
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
