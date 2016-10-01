package com.orcchg.musicsquare.ui.tab;

import com.domain.interactor.GetGenresList;
import com.domain.interactor.UseCase;
import com.domain.model.Genre;
import com.orcchg.musicsquare.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class TabPresenter extends BasePresenter<TabContract.View> implements TabContract.Presenter {

    private final GetGenresList getGenresListUseCase;

    @Inject
    TabPresenter(GetGenresList getGenresListUseCase) {
        this.getGenresListUseCase = getGenresListUseCase;
        this.getGenresListUseCase.setPostExecuteCallback(createGetGenresCallback());
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @Override
    public void loadGenres() {
        if (isViewAttached()) getView().showLoading();
        getGenresListUseCase.execute();
    }

    @Override
    public void retry() {
        // TODO: invalidate cached genres
        loadGenres();
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
}
