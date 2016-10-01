package com.orcchg.musicsquare.ui.tab;

import android.os.Bundle;

import com.domain.interactor.GetGenresList;
import com.domain.interactor.GetTotalGenres;
import com.domain.interactor.InvalidateGenreCache;
import com.domain.interactor.UseCase;
import com.domain.model.Genre;
import com.domain.model.TotalValue;
import com.orcchg.musicsquare.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class TabPresenter extends BasePresenter<TabContract.View> implements TabContract.Presenter {

    private final GetGenresList getGenresListUseCase;
    private final GetTotalGenres getTotalGenresUseCase;
    private final InvalidateGenreCache invalidateCacheUseCase;

    static class Memento {
        static final String BUNDLE_KEY_TOTAL_GENRES = "bundle_key_total_genres";

        int totalGenres = 0;

        void toBundle(Bundle outState) {
            outState.putInt(BUNDLE_KEY_TOTAL_GENRES, totalGenres);
        }

        static Memento fromBundle(Bundle savedInstanceState) {
            Memento memento = new Memento();
            memento.totalGenres = savedInstanceState.getInt(BUNDLE_KEY_TOTAL_GENRES);
            return memento;
        }
    }

    Memento memento;

    @Inject
    TabPresenter(GetGenresList getGenresListUseCase, GetTotalGenres getTotalGenresUseCase,
                 InvalidateGenreCache invalidateCacheUseCase) {
        this.getGenresListUseCase = getGenresListUseCase;
        this.getTotalGenresUseCase = getTotalGenresUseCase;
        this.invalidateCacheUseCase = invalidateCacheUseCase;
        this.getGenresListUseCase.setPostExecuteCallback(createGetGenresCallback());
        this.getTotalGenresUseCase.setPostExecuteCallback(createGetTotalCallback());
        this.invalidateCacheUseCase.setPostExecuteCallback(createInvalidateCacheCallback());
        this.memento = new Memento();
    }

    @DebugLog @Override
    public void onStart() {
        super.onStart();
        start();
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @DebugLog @Override
    public void retry() {
        invalidateCache();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    private void start() {
        if (memento.totalGenres <= 0) {
            getTotalGenresUseCase.execute();
        }
    }

    @DebugLog
    private void loadGenres() {
        if (isViewAttached()) {
            if (memento.totalGenres <= 0) {
                getView().showLoading();
            }
        }
        getGenresListUseCase.execute();
    }

    @DebugLog
    private void invalidateCache() {
        memento.totalGenres = 0;
        if (isViewAttached()) getView().showLoading();
        invalidateCacheUseCase.execute();
    }

    /* Callback */
    // --------------------------------------------------------------------------------------------
    private UseCase.OnPostExecuteCallback<List<Genre>> createGetGenresCallback() {
        return new UseCase.OnPostExecuteCallback<List<Genre>>() {
            @Override
            public void onFinish(List<Genre> genres) {
                if (isViewAttached()) getView().showTabs(genres);
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }

    private UseCase.OnPostExecuteCallback<TotalValue> createGetTotalCallback() {
        return new UseCase.OnPostExecuteCallback<TotalValue>() {
            @Override
            public void onFinish(TotalValue total) {
                Timber.i("Total genres: %s", total.getValue());
                memento.totalGenres = total.getValue();
                loadGenres();
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
