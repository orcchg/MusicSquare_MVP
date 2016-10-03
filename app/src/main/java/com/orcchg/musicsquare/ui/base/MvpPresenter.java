package com.orcchg.musicsquare.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);
    void detachView();

    void onCreate(@Nullable Bundle savedInstanceState);
    void onStart();
    void onResume();
    void onPause();
    void onSaveInstanceState(Bundle outState);
    void onStop();
    void onDestroy();
}
