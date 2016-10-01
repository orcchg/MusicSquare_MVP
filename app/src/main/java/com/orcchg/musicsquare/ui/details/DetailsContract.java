package com.orcchg.musicsquare.ui.details;

import com.orcchg.musicsquare.ui.base.MvpPresenter;
import com.orcchg.musicsquare.ui.base.MvpView;
import com.orcchg.musicsquare.ui.viewobject.ArtistDetailsVO;

interface DetailsContract {
    interface View extends MvpView {
        void setGrade(int grade);
        void showArtist(ArtistDetailsVO artist);
        void showError();
    }

    interface Presenter extends MvpPresenter<View> {
        void start();
        void loadArtistDetails();
    }
}
