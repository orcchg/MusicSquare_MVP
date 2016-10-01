package com.orcchg.musicsquare.ui.details;

import com.domain.interactor.GetArtistDetails;
import com.domain.interactor.UseCase;
import com.domain.model.Artist;
import com.domain.util.ArtistUtils;
import com.orcchg.musicsquare.ui.base.BasePresenter;
import com.orcchg.musicsquare.ui.viewobject.ArtistDetailsVO;
import com.orcchg.musicsquare.ui.viewobject.mapper.ArtistDetailsMapper;

import javax.inject.Inject;

import hugo.weaving.DebugLog;

public class DetailsPresenter extends BasePresenter<DetailsContract.View> implements DetailsContract.Presenter {

    private final GetArtistDetails getArtistDetailsUseCase;

    static class Memento {
    }

    Memento memento;

    @Inject
    DetailsPresenter(GetArtistDetails getArtistDetailsUseCase) {
        this.getArtistDetailsUseCase = getArtistDetailsUseCase;
        this.getArtistDetailsUseCase.setPostExecuteCallback(createGetDetailsCallback());
        this.memento = new Memento();
    }

    @DebugLog @Override
    public void onStart() {
        super.onStart();
        start();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    private void start() {
        // if (isViewAttached()) getView().showLoading();  // TODO: load details async
        getArtistDetailsUseCase.execute();
    }

    /* Callback */
    // --------------------------------------------------------------------------------------------
    private UseCase.OnPostExecuteCallback<Artist> createGetDetailsCallback() {
        return new UseCase.OnPostExecuteCallback<Artist>() {
            @Override
            public void onFinish(Artist artist) {
                ArtistDetailsMapper mapper = new ArtistDetailsMapper();
                ArtistDetailsVO artistVO = mapper.map(artist);
                int grade = ArtistUtils.calculateGrade(artist);
                if (isViewAttached()) {
                    getView().showArtist(artistVO);
                    getView().setGrade(grade);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) getView().showError();
            }
        };
    }
}
