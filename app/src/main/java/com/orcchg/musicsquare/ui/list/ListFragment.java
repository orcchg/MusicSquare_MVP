package com.orcchg.musicsquare.ui.list;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.BaseFragment;
import com.orcchg.musicsquare.ui.list.injection.DaggerListComponent;
import com.orcchg.musicsquare.ui.list.injection.ListComponent;
import com.orcchg.musicsquare.ui.util.ShadowHolder;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;
import com.orcchg.musicsquare.util.GridItemDecorator;
import com.orcchg.musicsquare.util.ViewUtility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListFragment extends BaseFragment<ListContract.View, ListContract.Presenter> implements ListContract.View {
    private static final String BUNDLE_KEY_GENRES = "bundle_key_genres";
    private static final String BUNDLE_KEY_LM_STATE = "bundle_key_lm_state";

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_musician_list) RecyclerView artistsList;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.loading_view) View loadingView;
    @BindView(R.id.error_view) View errorView;

    private ListComponent listComponent;
    private ListAdapter artistsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @OnClick(R.id.btn_retry)
    public void onRetryClick() {
        presenter.retry();
    }

    private ShadowHolder shadowHolder;
    private String[] genres;

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
        listComponent.inject(this);  // inject all injectable field here in {@link ListFragment}.
    }

    /* Lifecycle */
    // ------------------------------------------
    public static ListFragment newInstance(String... genres) {
        Bundle args = new Bundle();
        args.putStringArray(BUNDLE_KEY_GENRES, genres);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /* View */
    // ------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (ShadowHolder.class.isInstance(activity)) {
            shadowHolder = (ShadowHolder) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.genres = args.getStringArray(BUNDLE_KEY_GENRES);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_music_list, container, false);
        ButterKnife.bind(this, rootView);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.retry());

        if (ViewUtility.isLargeScreen(getActivity())) {
            layoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_span));
            artistsList.addItemDecoration(new GridItemDecorator(getActivity(), (R.dimen.grid_card_spacing)));
        } else {
            layoutManager = new LinearLayoutManager(getActivity());
        }

        artistsAdapter = new ListAdapter(new ArrayList<>(), this::openArtistDetails);
        artistsList.setAdapter(artistsAdapter);

        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(BUNDLE_KEY_LM_STATE);
            layoutManager.onRestoreInstanceState(state);
        }
        artistsList.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.loadArtists(this.genres);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable state = layoutManager.onSaveInstanceState();
        outState.putParcelable(BUNDLE_KEY_LM_STATE, state);
        super.onSaveInstanceState(outState);
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public void showArtists(List<ArtistListItemVO> artists) {
        swipeRefreshLayout.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);

        if (artists == null || artists.isEmpty()) {
            artistsList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            artistsList.setVisibility(View.VISIBLE);
            artistsAdapter.clear();
            artistsAdapter.populate(artists);
            artistsAdapter.notifyDataSetChanged();
        }

        if (shadowHolder != null) shadowHolder.showShadow(true);
    }

    @Override
    public void showError() {
        swipeRefreshLayout.setRefreshing(false);
        artistsList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        if (shadowHolder != null) shadowHolder.showShadow(true);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(false);
        artistsList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        if (shadowHolder != null) shadowHolder.showShadow(false);  // don't overlap with progress bar
    }

    @Override
    public void openArtistDetails(View view, long artistId) {
        this.navigator.openDetailsScreen(getActivity(), artistId,
                ViewUtility.isImageTransitionEnabled() ? view : null);
    }
}

