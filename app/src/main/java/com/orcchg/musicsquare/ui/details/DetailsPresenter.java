package com.orcchg.musicsquare.ui.details;

import com.domain.interactor.GetArtistDetails;
import com.domain.interactor.UseCase;
import com.domain.model.Artist;
import com.domain.util.ArtistUtils;
import com.orcchg.musicsquare.ui.base.BasePresenter;
import com.orcchg.musicsquare.ui.viewobject.ArtistDetailsVO;
import com.orcchg.musicsquare.ui.viewobject.mapper.ArtistDetailsMapper;

import javax.inject.Inject;

public class DetailsPresenter extends BasePresenter<DetailsContract.View> implements DetailsContract.Presenter {

    private final GetArtistDetails getArtistDetailsUseCase;

    @Inject
    DetailsPresenter(GetArtistDetails getArtistDetailsUseCase) {
        this.getArtistDetailsUseCase = getArtistDetailsUseCase;
        this.getArtistDetailsUseCase.setPostExecuteCallback(createGetDetailsCallback());
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @Override
    public void start() {
        loadArtistDetails();
    }

    @Override
    public void loadArtistDetails() {
//        if (isViewAttached()) getView().showLoading();  // TODO: load details async
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
