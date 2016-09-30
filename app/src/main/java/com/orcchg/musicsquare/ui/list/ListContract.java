package com.orcchg.musicsquare.ui.list;

import android.support.v7.widget.RecyclerView;

import com.orcchg.musicsquare.ui.IActivityProvider;
import com.orcchg.musicsquare.ui.MvpPresenter;
import com.orcchg.musicsquare.ui.MvpView;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

import java.util.List;

public interface ListContract {
    interface View extends MvpView, IActivityProvider {
        RecyclerView getListView();
        void showArtists(List<ArtistListItemVO> artists);
        void showError();
        void showLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadArtists();
        void loadArtists(int limit, int offset);
        void loadArtists(List<String> genres);
        void loadArtists(int limit, int offset, List<String> genres);
        void retry();
        void openArtistDetails(android.view.View view, long artistId);
        void onScroll(int itemsLeftToEnd);
        void setGenres(List<String> genres);
        void start();
    }
}
