package com.orcchg.musicsquare.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.domain.util.ArtistUtils;
import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.BaseActivity;
import com.orcchg.musicsquare.ui.details.injection.DaggerDetailsComponent;
import com.orcchg.musicsquare.ui.details.injection.DetailsComponent;
import com.orcchg.musicsquare.ui.details.injection.DetailsModule;
import com.orcchg.musicsquare.ui.viewobject.ArtistDetailsVO;
import com.orcchg.musicsquare.util.ImageTransform;
import com.orcchg.musicsquare.util.ViewUtility;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class DetailsActivity extends BaseActivity<DetailsContract.View, DetailsContract.Presenter> implements DetailsContract.View {
    public static final String EXTRA_ARTIST_ID = "extra_artist_id";

    @Nullable @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;  // disabled on tablets
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pb_loading) CircularProgressBar progressBar;
    @BindView(R.id.iv_cover) ImageView coverImageView;
    @BindView(R.id.tv_cover_error) TextView coverErrorTextView;
    @BindView(R.id.top_overlay) View topOverlayView;
    @BindView(R.id.bottom_overlay) View bottomOverlayView;
    @BindView(R.id.tv_description) TextView descriptionTextView;
    @BindView(R.id.tv_link) TextView linkTextView;
    @BindView(R.id.tv_genres) TextView genresTextView;
    @BindView(R.id.tv_tracks_count) TextView tracksCountTextView;
    @BindView(R.id.tv_albums_count) TextView albumsCountTextView;
    @BindViews({R.id.iv_star_1, R.id.iv_star_2, R.id.iv_star_3, R.id.iv_star_4, R.id.iv_star_5}) List<ImageView> starViews;

    private Drawable strokeStar;
    private Drawable halfStar;
    private Drawable fullStar;

    private long artistId = ArtistUtils.BAD_ARTIST_ID;
    private DetailsComponent detailsComponent;

    @NonNull
    @Override
    protected DetailsContract.Presenter createPresenter() {
        return detailsComponent.presenter();
    }

    public static Intent getStartIntent(Context context, long artistId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(EXTRA_ARTIST_ID, artistId);
        return intent;
    }

    @Override
    protected void injectDependencies() {
        /**
         * Create concrete {@link DetailsComponent} dagger-implementation with {@link DetailsModule}
         * passing {@link DetailsActivity#artistId} as argument to configure instances.
         */
        detailsComponent = DaggerDetailsComponent.builder()
                .applicationComponent(getApplicationComponent())
                .detailsModule(new DetailsModule(artistId))
                .build();
        detailsComponent.inject(this);  // inject all injectable field here in {@link DetailsActivity}.
    }

    /* Lifecycle */
    // ------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        artistId = getIntent().getLongExtra(EXTRA_ARTIST_ID, ArtistUtils.BAD_ARTIST_ID);
        if (ViewUtility.isLargeScreen(this)) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);  // removes title from Activity as Dialog
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_details);
        ButterKnife.bind(this);
        initToolbar();
        initResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResources();
    }

    /* View */
    // ------------------------------------------
    private void initToolbar() {
        if (collapsingToolbar != null) {
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        }
        toolbar.setNavigationOnClickListener((view) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                supportFinishAfterTransition();
            } else {
                finish();
            }
        });
    }

    private void initResources() {
        Drawable strokeStar1 = getResources().getDrawable(R.drawable.ic_star_border_white_24dp);
        Drawable halfStar1 = getResources().getDrawable(R.drawable.ic_star_half_white_24dp);
        Drawable fullStar1 = getResources().getDrawable(R.drawable.ic_star_white_24dp);

        strokeStar = DrawableCompat.wrap(strokeStar1);
        halfStar = DrawableCompat.wrap(halfStar1);
        fullStar = DrawableCompat.wrap(fullStar1);

        @ColorInt int color = getResources().getColor(R.color.star_color);
        DrawableCompat.setTint(strokeStar, color);
        DrawableCompat.setTint(halfStar, color);
        DrawableCompat.setTint(fullStar, color);
    }

    private void releaseResources() {
        DrawableCompat.unwrap(strokeStar);
        DrawableCompat.unwrap(halfStar);
        DrawableCompat.unwrap(fullStar);
    }

    void startProgressAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        topOverlayView.setVisibility(View.INVISIBLE);
        bottomOverlayView.setVisibility(View.INVISIBLE);
        coverErrorTextView.setVisibility(View.GONE);
    }

    void stopProgressAnimation() {
        progressBar.setVisibility(View.GONE);
        topOverlayView.setVisibility(View.VISIBLE);
        bottomOverlayView.setVisibility(View.VISIBLE);
        coverErrorTextView.setVisibility(View.GONE);
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public void setGrade(int grade) {
        int quotient = grade / 2;
        int residual = grade % 2;

        for (int i = 0; i < quotient; ++i) {
            starViews.get(i).setBackgroundDrawable(fullStar);
        }
        if (residual > 0) {
            starViews.get(quotient).setBackgroundDrawable(halfStar);
        }
        for (int i = quotient + residual; i < starViews.size(); ++i) {
            starViews.get(i).setBackgroundDrawable(strokeStar);
        }
    }

    @Override
    public void showArtist(ArtistDetailsVO artist) {
        if (collapsingToolbar != null) { collapsingToolbar.setTitle(artist.getName()); }
        toolbar.setTitle(artist.getName());
        genresTextView.setText(TextUtils.join(", ", artist.getGenres()));
        tracksCountTextView.setText(String.format(getResources().getString(R.string.str_tracks_count), Integer.toString(artist.getTracksCount())));
        albumsCountTextView.setText(String.format(getResources().getString(R.string.str_albums_count), Integer.toString(artist.getAlbumsCount())));
        descriptionTextView.setText(artist.getDescription());
        linkTextView.setText(artist.getWebLink());
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        startProgressAnimation();

        Glide.with(this)
                .load(artist.getCoverLarge())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        showError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        stopProgressAnimation();
                        return false;
                    }
                })
                .transform(ImageTransform.create(this, ImageTransform.TOP_CROP))
                .into(coverImageView);
    }

    @Override
    public void showError() {
        progressBar.setVisibility(View.GONE);
        topOverlayView.setVisibility(View.INVISIBLE);
        bottomOverlayView.setVisibility(View.INVISIBLE);
        coverErrorTextView.setVisibility(View.VISIBLE);
    }
}
