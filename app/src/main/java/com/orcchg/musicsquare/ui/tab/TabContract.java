package com.orcchg.musicsquare.ui.tab;

import com.orcchg.musicsquare.ui.MvpPresenter;
import com.orcchg.musicsquare.ui.MvpView;

import java.util.List;

public interface TabContract {
    interface View extends MvpView {
        void showTabs(List<String[]> titles);
        void showError();
        void showLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadGenres();
        void retry();
    }
}
