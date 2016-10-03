package com.orcchg.musicsquare.ui.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.domain.interactor.GetArtistList;
import com.domain.interactor.GetTotalArtists;
import com.domain.interactor.InvalidateArtistCache;
import com.domain.interactor.UseCase;
import com.domain.model.Artist;
import com.domain.model.TotalValue;
import com.orcchg.musicsquare.ui.base.BasePresenter;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;
import com.orcchg.musicsquare.ui.viewobject.mapper.ArtistListItemMapper;
import com.orcchg.musicsquare.util.ViewUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class ListPresenter extends BasePresenter<ListContract.View> implements ListContract.Presenter {
    private static final int LIMIT_PER_REQUEST = 20;
    private static final int LOAD_MORE_THRESHOLD = 1;

    private ListAdapter artistsAdapter;

    private final GetArtistList getArtistListUseCase;
    private final GetTotalArtists getTotalArtistsUseCase;
    private final InvalidateArtistCache invalidateCacheUseCase;

    static class Memento {
        static final String BUNDLE_KEY_CURRENT_SIZE = "bundle_key_current_size";
        static final String BUNDLE_KEY_CURRENT_OFFSET = "bundle_key_current_offset";
        static final String BUNDLE_KEY_TOTAL_ARTISTS = "bundle_key_total_artists";
        static final String BUNDLE_KEY_GENRES = "bundle_key_genres";

        int currentSize = 0;
        int currentOffset = 0;
        int totalArtists = 0;
        List<String> genres;

        void toBundle(Bundle outState) {
            outState.putInt(BUNDLE_KEY_CURRENT_SIZE, currentSize);
            outState.putInt(BUNDLE_KEY_CURRENT_OFFSET, currentOffset);
            outState.putInt(BUNDLE_KEY_TOTAL_ARTISTS, totalArtists);
            outState.putStringArrayList(BUNDLE_KEY_GENRES, (ArrayList<String>) genres);
        }

        static Memento fromBundle(Bundle savedInstanceState) {
            Memento memento = new Memento();
            memento.currentSize = savedInstanceState.getInt(BUNDLE_KEY_CURRENT_SIZE);
            memento.currentOffset = savedInstanceState.getInt(BUNDLE_KEY_CURRENT_OFFSET);
            memento.totalArtists = savedInstanceState.getInt(BUNDLE_KEY_TOTAL_ARTISTS);
            memento.genres = savedInstanceState.getStringArrayList(BUNDLE_KEY_GENRES);
            return memento;
        }
    }

    private Memento memento;

    /**
     * Constructs an instance of {@link ListPresenter}.
     *
     * Marked as {@link Inject} because this constructor will be used
     * to create an instance of the {@link ListPresenter} class to
     * provide to {@link ListActivity}.
     *
     * @param getArtistListUseCase possible use case for this presenter
     */
    @Inject
    ListPresenter(GetArtistList getArtistListUseCase, GetTotalArtists getTotalArtistsUseCase,
                  InvalidateArtistCache invalidateCacheUseCase) {
        this.artistsAdapter = new ListAdapter(this::openArtistDetails);
        this.artistsAdapter.setOnErrorClickListener((view) -> retryLoadMore());
        this.getArtistListUseCase = getArtistListUseCase;
        this.getTotalArtistsUseCase = getTotalArtistsUseCase;
        this.invalidateCacheUseCase = invalidateCacheUseCase;
        this.getArtistListUseCase.setPostExecuteCallback(createGetListCallback());
        this.getTotalArtistsUseCase.setPostExecuteCallback(createGetTotalCallback());
        this.invalidateCacheUseCase.setPostExecuteCallback(createInvalidateCacheCallback());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            memento = Memento.fromBundle(savedInstanceState);
        } else {
            this.memento = new Memento();
        }
    }

    @DebugLog @Override
    public void onStart() {
        super.onStart();
        if (isViewAttached()) {
            RecyclerView list = getView().getListView();
            if (list.getAdapter() == null) {
                list.setAdapter(artistsAdapter);
            }
        }
        start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        memento.toBundle(outState);
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @DebugLog @Override
    public void retry() {
        invalidateCache();
    }

    @DebugLog @Override
    public void onScroll(int itemsLeftToEnd) {
        if (isThereMore() && itemsLeftToEnd <= LOAD_MORE_THRESHOLD) {
            memento.currentOffset += LIMIT_PER_REQUEST;
            loadArtists(LIMIT_PER_REQUEST, memento.currentOffset, memento.genres);
        }
    }

    @DebugLog @Override
    public void setGeServernres(@Nullable List<String> genres) {
        memento.genres = genres;
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    private void start() {
        if (isStateRestored()) {
            int limit = memento.currentSize;
            memento.currentSize = 0;
            loadArtists(limit, 0, memento.genres);
        } else if (memento.totalArtists <= 0) {
            artistsAdapter.clear();

            GetTotalArtists.Parameters parameters = new GetTotalArtists.Parameters.Builder()
                    .setGenres(memento.genres)
                    .build();
            getTotalArtistsUseCase.setParameters(parameters);
            getTotalArtistsUseCase.execute();
        }
    }

    @DebugLog
    private void retryLoadMore() {
        artistsAdapter.onError(false);  // show loading more
        loadArtists(LIMIT_PER_REQUEST, memento.currentOffset, memento.genres);
    }

    @DebugLog
    private void loadArtists() {
        loadArtists(-1, 0);
    }

    @DebugLog
    private void loadArtists(int limit, int offset) {
        loadArtists(limit, offset, null);
    }

    @DebugLog
    private void loadArtists(@Nullable List<String> genres) {
        loadArtists(-1, 0, genres);
    }

    @DebugLog
    private void loadArtists(int limit, int offset, @Nullable List<String> genres) {
        memento.genres = genres;
        if (isViewAttached()) {
            if (memento.totalArtists <= 0) {
                getView().showLoading();
            }
        }
        GetArtistList.Parameters parameters = new GetArtistList.Parameters.Builder()
                .setLimit(limit)
                .setOffset(offset)
                .setGenres(genres)
                .build();
        getArtistListUseCase.setParameters(parameters);
        getArtistListUseCase.execute();
    }

    private void openArtistDetails(View view, long artistId) {
        if (isViewAttached()) {
            Activity activity = getView().getActivity();
            navigator.openDetailsScreen(activity, artistId,
                    ViewUtility.isImageTransitionEnabled() ? view : null);
        }
    }

    @DebugLog
    private void invalidateCache() {
        memento.currentSize = 0;
        memento.currentOffset = 0;
        memento.totalArtists = 0;
        if (isViewAttached()) getView().showLoading();
        invalidateCacheUseCase.execute();
    }

    @DebugLog
    private boolean isThereMore() {
        return memento.totalArtists > memento.currentSize + memento.currentOffset;
    }

    /* Callback */
    // --------------------------------------------------------------------------------------------
    private UseCase.OnPostExecuteCallback<List<Artist>> createGetListCallback() {
        return new UseCase.OnPostExecuteCallback<List<Artist>>() {
            @Override
            public void onFinish(List<Artist> artists) {
                memento.currentSize += artists.size();
                ArtistListItemMapper mapper = new ArtistListItemMapper();
                List<ArtistListItemVO> artistsVO = mapper.map(artists);
                artistsAdapter.populate(artistsVO, isThereMore());
                if (isViewAttached()) {
                    getView().showArtists(artistsVO);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (memento.currentSize <= 0) {
                    if (isViewAttached()) getView().showError();
                } else {
                    artistsAdapter.onError(true);
                }
            }
        };
    }

    private UseCase.OnPostExecuteCallback<TotalValue> createGetTotalCallback() {
        return new UseCase.OnPostExecuteCallback<TotalValue>() {
            @Override
            public void onFinish(TotalValue total) {
                Timber.i("Total artists: %s", total.getValue());
                memento.totalArtists = total.getValue();
                loadArtists(LIMIT_PER_REQUEST, 0, memento.genres);
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }

    private UseCase.OnPostExecuteCallback<Boolean> createInvalidateCacheCallback() {
        return new UseCase.OnPostExecuteCallback<Boolean>() {
            @Override
            public void onFinish(Boolean result) {
                start();
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }
}
