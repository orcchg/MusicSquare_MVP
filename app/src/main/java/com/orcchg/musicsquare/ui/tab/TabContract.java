package com.orcchg.musicsquare.ui.tab;

import com.domain.model.Genre;
import com.orcchg.musicsquare.ui.MvpPresenter;
import com.orcchg.musicsquare.ui.MvpView;

import java.util.List;

public interface TabContract {
    interface View extends MvpView {
        void showTabs(List<Genre> genres);
        void showError();
        void showLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadGenres();
        void retry();
    }
}
