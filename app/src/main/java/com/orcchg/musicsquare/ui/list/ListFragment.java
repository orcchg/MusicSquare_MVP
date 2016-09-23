package com.orcchg.musicsquare.ui.list;

import android.os.Bundle;
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

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_musician_list) RecyclerView artistsList;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.loading_view) View loadingView;
    @BindView(R.id.error_view) View errorView;

    private ListComponent listComponent;
    private ListAdapter artistsAdapter;

    @OnClick(R.id.btn_retry)
    public void onRetryClick() {
        presenter.retry();
    }

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        genres = args.getStringArray(BUNDLE_KEY_GENRES);
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
            artistsList.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_span)));
            artistsList.addItemDecoration(new GridItemDecorator(getActivity(), (R.dimen.grid_card_spacing)));
        } else {
            artistsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        artistsAdapter = new ListAdapter(new ArrayList<>(), this::openArtistDetails);
        artistsList.setAdapter(artistsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.loadArtists();
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public void showArtists(List<ArtistListItemVO> artists) {
        swipeRefreshLayout.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);

        if (artists == null || artists.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            artistsList.setVisibility(View.VISIBLE);
            artistsAdapter.clear();
            artistsAdapter.populate(artists);
            artistsAdapter.notifyDataSetChanged();
        }

        ListActivity activity = (ListActivity) getActivity();
        activity.showShadow(true);
    }

    @Override
    public void showError() {
        swipeRefreshLayout.setRefreshing(false);
        artistsList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        ListActivity activity = (ListActivity) getActivity();
        activity.showShadow(true);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(false);
        artistsList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        ListActivity activity = (ListActivity) getActivity();
        activity.showShadow(false);  // don't overlap with progress bar
    }

    @Override
    public void openArtistDetails(View view, long artistId) {
        this.navigator.openDetailsScreen(getActivity(), artistId,
                ViewUtility.isImageTransitionEnabled() ? view : null);
    }
}

