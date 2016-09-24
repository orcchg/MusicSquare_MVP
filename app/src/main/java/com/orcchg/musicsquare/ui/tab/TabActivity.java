package com.orcchg.musicsquare.ui.tab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.BaseActivity;
import com.orcchg.musicsquare.ui.tab.injection.DaggerTabComponent;
import com.orcchg.musicsquare.ui.tab.injection.TabComponent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabActivity extends BaseActivity<TabContract.View, TabContract.Presenter> implements TabContract.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.loading_view) View loadingView;
    @BindView(R.id.error_view) View errorView;
    @BindView(R.id.rl_toolbar_dropshadow) View dropshadowView;

    private TabComponent tabComponent;
    private TabAdapter tabsAdapter;

    public static Intent getCallingIntent(@NonNull Context context) {
        return new Intent(context, TabActivity.class);
    }

    @NonNull @Override
    protected TabContract.Presenter createPresenter() {
        return tabComponent.presenter();
    }

    @Override
    protected void injectDependencies() {
        tabComponent = DaggerTabComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();
        tabComponent.inject(this);
    }

    /* Lifecycle */
    // ------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        ButterKnife.bind(this);
        initView();
        initToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.loadGenres();
    }

    /* View */
    // ------------------------------------------
    private void initView() {
        tabsAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void initToolbar() {
        toolbar.setNavigationOnClickListener((view) -> finish());
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public void showTabs(List<String[]> titles) {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);

        if (titles == null || titles.isEmpty()) {
            dropshadowView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        } else {
            dropshadowView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            tabsAdapter.setTabs(titles);
            tabsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showError() {
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        dropshadowView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        dropshadowView.setVisibility(View.INVISIBLE);
    }
}
