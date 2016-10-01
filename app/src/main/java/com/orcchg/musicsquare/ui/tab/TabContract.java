package com.orcchg.musicsquare.ui.tab;

import com.domain.model.Genre;
import com.orcchg.musicsquare.ui.base.MvpPresenter;
import com.orcchg.musicsquare.ui.base.MvpView;

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
