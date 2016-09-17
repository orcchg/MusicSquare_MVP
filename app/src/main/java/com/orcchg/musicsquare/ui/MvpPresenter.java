package com.orcchg.musicsquare.ui;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);
    void detachView();
}
