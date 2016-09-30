package com.orcchg.musicsquare.ui.list;

import android.app.Activity;
import android.view.View;

import com.domain.interactor.GetArtistList;
import com.domain.interactor.GetTotalArtists;
import com.domain.interactor.InvalidateCache;
import com.domain.interactor.UseCase;
import com.domain.model.Artist;
import com.domain.model.TotalValue;
import com.orcchg.musicsquare.ui.BasePresenter;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;
import com.orcchg.musicsquare.ui.viewobject.mapper.ArtistListItemMapper;
import com.orcchg.musicsquare.util.ViewUtility;

import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class ListPresenter extends BasePresenter<ListContract.View> implements ListContract.Presenter {
    private static final int LIMIT_PER_REQUEST = 20;
    private static final int LOAD_MORE_THRESHOLD = 1;

    ListAdapter artistsAdapter;

    private final GetArtistList getArtistListUseCase;
    private final GetTotalArtists getTotalArtistsUseCase;
    private final InvalidateCache invalidateCacheUseCase;

    int currentSize = 0;
    int currentOffset = 0;
    int totalArtists = 0;
    List<String> genres;

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
                  InvalidateCache invalidateCacheUseCase) {
        this.artistsAdapter = new ListAdapter(this::openArtistDetails);
        this.getArtistListUseCase = getArtistListUseCase;
        this.getTotalArtistsUseCase = getTotalArtistsUseCase;
        this.invalidateCacheUseCase = invalidateCacheUseCase;
        this.getArtistListUseCase.setPostExecuteCallback(createGetListCallback());
        this.getTotalArtistsUseCase.setPostExecuteCallback(createGetTotalCallback());
        this.invalidateCacheUseCase.setPostExecuteCallback(createInvalidateCacheCallback());
    }

    @DebugLog @Override
    public void onStart() {
        super.onCreate();
        if (isViewAttached()) getView().getListView().setAdapter(artistsAdapter);
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @DebugLog @Override
    public void loadArtists() {
        loadArtists(-1, 0);
    }

    @DebugLog @Override
    public void loadArtists(int limit, int offset) {
        loadArtists(limit, offset, null);
    }

    @DebugLog @Override
    public void loadArtists(List<String> genres) {
        loadArtists(-1, 0, genres);
    }

    @DebugLog @Override
    public void loadArtists(int limit, int offset, List<String> genres) {
        this.genres = genres;
        if (isViewAttached()) {
            if (totalArtists <= 0) {
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

    @DebugLog @Override
    public void retry() {
        invalidateCache();
    }

    @DebugLog @Override
    public void openArtistDetails(View view, long artistId) {
        if (isViewAttached()) {
            Activity activity = getView().getActivity();
            navigator.openDetailsScreen(activity, artistId,
                    ViewUtility.isImageTransitionEnabled() ? view : null);
        }
    }

    @DebugLog @Override
    public void onScroll(int itemsLeftToEnd) {
        if (isThereMore() && itemsLeftToEnd <= LOAD_MORE_THRESHOLD) {
            currentOffset += LIMIT_PER_REQUEST;
            loadArtists(LIMIT_PER_REQUEST, currentOffset, genres);
        }
    }

    @DebugLog @Override
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @DebugLog @Override
    public void start() {
        if (totalArtists <= 0) {
            artistsAdapter.clear();
            getTotalArtistsUseCase.execute();
        } else {
            loadArtists(LIMIT_PER_REQUEST, 0, genres);
        }
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    private void invalidateCache() {
        currentSize = 0;
        currentOffset = 0;
        totalArtists = 0;
        if (isViewAttached()) getView().showLoading();
        invalidateCacheUseCase.execute();
    }

    boolean isThereMore() {
        return totalArtists > currentSize + currentOffset;
    }

    /* Callback */
    // --------------------------------------------------------------------------------------------
    private UseCase.OnPostExecuteCallback<List<Artist>> createGetListCallback() {
        return new UseCase.OnPostExecuteCallback<List<Artist>>() {
            @DebugLog @Override
            public void onFinish(List<Artist> artists) {
                currentSize += artists.size();
                ArtistListItemMapper mapper = new ArtistListItemMapper();
                List<ArtistListItemVO> artistsVO = mapper.map(artists);
                artistsAdapter.populate(artistsVO, isThereMore());
                if (isViewAttached()) {
                    getView().showArtists(artistsVO);
                }
            }

            @DebugLog @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }

    private UseCase.OnPostExecuteCallback<TotalValue> createGetTotalCallback() {
        return new UseCase.OnPostExecuteCallback<TotalValue>() {
            @DebugLog @Override
            public void onFinish(TotalValue total) {
                Timber.d("Total artists: %s", total.getValue());
                totalArtists = total.getValue();
                loadArtists(LIMIT_PER_REQUEST, 0, genres);
            }

            @DebugLog @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }

    private UseCase.OnPostExecuteCallback<Boolean> createInvalidateCacheCallback() {
        return new UseCase.OnPostExecuteCallback<Boolean>() {
            @DebugLog @Override
            public void onFinish(Boolean result) {
                start();
            }

            @DebugLog @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }
}
