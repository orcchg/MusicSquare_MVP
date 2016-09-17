package com.orcchg.musicsquare.ui.list;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.BaseActivity;
import com.orcchg.musicsquare.ui.list.injection.DaggerListComponent;
import com.orcchg.musicsquare.ui.list.injection.ListComponent;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;
import com.orcchg.musicsquare.util.GridItemDecorator;
import com.orcchg.musicsquare.util.ViewUtility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends BaseActivity<ListContract.View, ListContract.Presenter> implements ListContract.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rl_toolbar_dropshadow) View dropshadowView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_musician_list) RecyclerView artistsList;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.loading_view) View loadingView;
    @BindView(R.id.error_view) View errorView;

    private ListComponent listComponent;
    private ListAdapter artistsAdapter;  // TODO: inject
    
    @OnClick(R.id.btn_retry)
    public void onRetryClick() {
        presenter.retry();
    }

    @NonNull
    @Override
    protected ListContract.Presenter createPresenter() {
        return listComponent.presenter();
    }

    @Override
    protected void injectDependencies() {
        /**
         * Create concrete {@link ListComponent} dagger-implementation directly, because
         * the component has no-args constructor.
         */
        listComponent = DaggerListComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();
        listComponent.inject(this);  // inject all injectable field here in {@link ListActivity}.
    }

    /* Lifecycle */
    // ------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);
        initView();
        initToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.loadArtists();
    }
    
    /* View */
    // ------------------------------------------
    private void initView() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.retry();
            }
        });

        if (ViewUtility.isLargeScreen(this)) {
            artistsList.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_span)));
            artistsList.addItemDecoration(new GridItemDecorator(this, (R.dimen.grid_card_spacing)));
        } else {
            artistsList.setLayoutManager(new LinearLayoutManager(this));
        }
        
        artistsAdapter = new ListAdapter(new ArrayList<ArtistListItemVO>(), (view, artistId) -> { presenter.openArtistDetails(view, artistId); });
        artistsList.setAdapter(artistsAdapter);
    }
    
    private void initToolbar() {
        toolbar.setTitle(R.string.str_musicians_list);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.getMenu().findItem(R.id.animation).setChecked(ViewUtility.isImageTransitionEnabled());
        toolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.about:
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.str_about)
                            .setMessage(R.string.str_about_message)
                            .setPositiveButton(R.string.str_ok, (dialog, which) -> dialog.dismiss()).show();
                    return true;
                case R.id.animation:
                    boolean checked = !ViewUtility.isImageTransitionEnabled();
                    ViewUtility.enableImageTransition(checked);
                    item.setChecked(checked);
                    return true;
            }
            return false;
        });

        Drawable overflowOriginal = toolbar.getOverflowIcon();
        if (overflowOriginal != null) {
            Drawable overflow = DrawableCompat.wrap(overflowOriginal);
            DrawableCompat.setTint(overflow, Color.WHITE);
            toolbar.setOverflowIcon(overflow);
        }
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public void showArtists(List<ArtistListItemVO> artists) {
        swipeRefreshLayout.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        dropshadowView.setVisibility(View.VISIBLE);

        if (artists == null || artists.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            artistsList.setVisibility(View.VISIBLE);
            artistsAdapter.clear();
            artistsAdapter.populate(artists);
            artistsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showError() {
        swipeRefreshLayout.setRefreshing(false);
        artistsList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        dropshadowView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(false);
        artistsList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        dropshadowView.setVisibility(View.INVISIBLE);  // don't overlap with progress bar
    }
}
