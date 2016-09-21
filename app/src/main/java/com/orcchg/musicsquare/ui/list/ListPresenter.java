package com.orcchg.musicsquare.ui.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.domain.interactor.GetArtistList;
import com.domain.interactor.InvalidateCache;
import com.domain.interactor.UseCase;
import com.domain.model.Artist;
import com.orcchg.musicsquare.ui.BasePresenter;
import com.orcchg.musicsquare.ui.details.DetailsActivity;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;
import com.orcchg.musicsquare.ui.viewobject.mapper.ArtistListItemMapper;
import com.orcchg.musicsquare.util.ViewUtility;

import java.util.List;

import javax.inject.Inject;

public class ListPresenter extends BasePresenter<ListContract.View> implements ListContract.Presenter {

    private final GetArtistList getArtistListUseCase;
    private final InvalidateCache invalidateCacheUseCase;

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
    ListPresenter(GetArtistList getArtistListUseCase, InvalidateCache invalidateCacheUseCase) {
        this.getArtistListUseCase = getArtistListUseCase;
        this.invalidateCacheUseCase = invalidateCacheUseCase;
        this.getArtistListUseCase.setPostExecuteCallback(createGetListCallback());
        this.invalidateCacheUseCase.setPostExecuteCallback(createInvalidateCacheCallback());
    }

    @Override
    public void loadArtists() {
        if (isViewAttached()) getView().showLoading();
        this.getArtistListUseCase.execute();
    }

    @Override
    public void openArtistDetails(View view, long artistId) {
        if (isViewAttached()) {
            Activity context = (Activity) getView();
            Intent intent = DetailsActivity.getStartIntent(context, artistId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ViewUtility.isImageTransitionEnabled()) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, "profile");
                context.startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void retry() {
        invalidateCache();
    }

    private void invalidateCache() {
        if (isViewAttached()) getView().showLoading();
        this.invalidateCacheUseCase.execute();
    }

    private UseCase.OnPostExecuteCallback<List<Artist>> createGetListCallback() {
        return new UseCase.OnPostExecuteCallback<List<Artist>>() {
            @Override
            public void onFinish(List<Artist> artists) {
                ArtistListItemMapper mapper = new ArtistListItemMapper();
                List<ArtistListItemVO> artistsVO = mapper.map(artists);
                if (isViewAttached()) getView().showArtists(artistsVO);
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }

    private UseCase.OnPostExecuteCallback createInvalidateCacheCallback() {
        return new UseCase.OnPostExecuteCallback() {
            @Override
            public void onFinish(Object values) {
                loadArtists();
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }
}
