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
import com.orcchg.musicsquare.ui.base.BaseFragment;
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
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class ListFragment extends BaseFragment<ListContract.View, ListContract.Presenter> implements ListContract.View {
    private static final String BUNDLE_KEY_GENRES = "bundle_key_genres";

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_musician_list) RecyclerView artistsList;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.loading_view) View loadingView;
    @BindView(R.id.error_view) View errorView;
    @OnClick(R.id.btn_retry)
    public void onRetryClick() {
        presenter.retry();
    }

    private ListComponent listComponent;
    private LinearLayoutManager layoutManager;

    private int lastVisible = -1;

    private ShadowHolder shadowHolder;

    static class Memento {
        private static final String BUNDLE_KEY_LM_STATE = "bundle_key_lm_state";

        Parcelable layoutManagerState;

        void toBundle(@NonNull Bundle outState) {
            outState.putParcelable(BUNDLE_KEY_LM_STATE, layoutManagerState);
        }

        static Memento fromBundle(@NonNull Bundle savedInstanceState) {
            Memento memento = new Memento();
            memento.layoutManagerState = savedInstanceState.getParcelable(BUNDLE_KEY_LM_STATE);
            return memento;
        }
    }

    Memento memento = new Memento();

    @NonNull @Override
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
    public static ListFragment newInstance() {
        return newInstance(null);
    }

    public static ListFragment newInstance(@Nullable ArrayList<String> genres) {
        Bundle args = new Bundle();
        args.putStringArrayList(BUNDLE_KEY_GENRES, genres);
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
        List<String> genres = args.getStringArrayList(BUNDLE_KEY_GENRES);
        presenter.setGenres(genres);
    }

    @Nullable @Override
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

        if (savedInstanceState != null) {
            memento = Memento.fromBundle(savedInstanceState);
        }
        artistsList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                processListScroll(recyclerView, dx, dy);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isStateRestored() && memento.layoutManagerState != null) {
            Timber.i("Restored state of layout manager");
            layoutManager.onRestoreInstanceState(memento.layoutManagerState);
        }
        artistsList.setLayoutManager(layoutManager);
    }

    @DebugLog @Override
    public void onSaveInstanceState(Bundle outState) {
        memento.layoutManagerState = layoutManager.onSaveInstanceState();
        memento.toBundle(outState);
        super.onSaveInstanceState(outState);
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public RecyclerView getListView() {
        return artistsList;
    }

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

    /* Internal */
    // --------------------------------------------------------------------------------------------
    void processListScroll(RecyclerView recyclerView, int dx, int dy) {
        if (dy <= 0) {
            return;  // skip scroll up
        }

        int last = layoutManager.findLastVisibleItemPosition();
        if (lastVisible == last) {
            return;  // skip scroll due to layout
        }

        lastVisible = last;
        int total = layoutManager.getItemCount();
        presenter.onScroll(total - last);
    }
}

