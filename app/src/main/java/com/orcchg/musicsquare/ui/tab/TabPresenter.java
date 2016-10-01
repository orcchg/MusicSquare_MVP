package com.orcchg.musicsquare.ui.tab;

import com.domain.interactor.GetGenresList;
import com.domain.interactor.GetTotalGenres;
import com.domain.interactor.InvalidateGenreCache;
import com.domain.interactor.UseCase;
import com.domain.model.Genre;
import com.domain.model.TotalValue;
import com.orcchg.musicsquare.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class TabPresenter extends BasePresenter<TabContract.View> implements TabContract.Presenter {

    private final GetGenresList getGenresListUseCase;
    private final GetTotalGenres getTotalGenresUseCase;
    private final InvalidateGenreCache invalidateCacheUseCase;

    private int totalGenres = 0;

    @Inject
    TabPresenter(GetGenresList getGenresListUseCase, GetTotalGenres getTotalGenresUseCase,
                 InvalidateGenreCache invalidateCacheUseCase) {
        this.getGenresListUseCase = getGenresListUseCase;
        this.getTotalGenresUseCase = getTotalGenresUseCase;
        this.invalidateCacheUseCase = invalidateCacheUseCase;
        this.getGenresListUseCase.setPostExecuteCallback(createGetGenresCallback());
        this.getTotalGenresUseCase.setPostExecuteCallback(createGetTotalCallback());
        this.invalidateCacheUseCase.setPostExecuteCallback(createInvalidateCacheCallback());
    }

    @Override
    public void onStart() {
        super.onStart();
        start();
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @Override
    public void loadGenres() {
        if (isViewAttached()) {
            if (totalGenres <= 0) {
                getView().showLoading();
            }
        }
        getGenresListUseCase.execute();
    }

    @Override
    public void retry() {
        invalidateCache();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    private void start() {
        if (totalGenres <= 0) {
            getTotalGenresUseCase.execute();
        }
    }

    private void invalidateCache() {
        totalGenres = 0;
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
                totalGenres = total.getValue();
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
