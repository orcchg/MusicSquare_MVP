package com.orcchg.musicsquare.ui.base;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);
    void detachView();

    void onCreate();
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
}
