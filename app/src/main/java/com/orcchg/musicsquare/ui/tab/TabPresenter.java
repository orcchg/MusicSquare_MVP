package com.orcchg.musicsquare.ui.tab;

import com.domain.interactor.GetGenresList;
import com.domain.interactor.UseCase;
import com.orcchg.musicsquare.ui.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TabPresenter extends BasePresenter<TabContract.View> implements TabContract.Presenter {

    private final GetGenresList getGenresListUseCase;

    @Inject
    TabPresenter(GetGenresList getGenresListUseCase) {
        this.getGenresListUseCase = getGenresListUseCase;
        this.getGenresListUseCase.setPostExecuteCallback(createGetGenresCallback());
    }

    @Override
    public void loadGenres() {
        if (isViewAttached()) getView().showLoading();
        this.getGenresListUseCase.execute();
    }

    @Override
    public void retry() {
        invalidateCache();
    }

    private void invalidateCache() {
        if (isViewAttached()) getView().showLoading();
        // TODO this.invalidateCacheUseCase.execute();
    }

    private UseCase.OnPostExecuteCallback<List<String>> createGetGenresCallback() {
        return new UseCase.OnPostExecuteCallback<List<String>>() {
            @Override
            public void onFinish(List<String> genres) {
                // TODO: merge duplicates
                List<String[]> titles = new ArrayList<>();
                for (String genre : genres) {
                    String[] array = new String[]{genre};
                    titles.add(array);
                }
                if (isViewAttached()) getView().showTabs(titles);
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }
}
