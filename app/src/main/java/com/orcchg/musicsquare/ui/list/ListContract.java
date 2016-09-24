package com.orcchg.musicsquare.ui.list;

import com.orcchg.musicsquare.ui.MvpPresenter;
import com.orcchg.musicsquare.ui.MvpView;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

import java.util.List;

public interface ListContract {
    interface View extends MvpView {
        void openArtistDetails(android.view.View view, long artistId);
        void showArtists(List<ArtistListItemVO> artists);
        void showError();
        void showLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadArtists();
        void loadArtists(int limit, int offset);
        void loadArtists(String... genres);
        void loadArtists(int limit, int offset, String... genres);
        void retry();
    }
}
