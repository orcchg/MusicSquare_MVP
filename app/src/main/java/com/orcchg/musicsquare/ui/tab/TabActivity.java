package com.orcchg.musicsquare.ui.tab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.BaseActivity;
import com.orcchg.musicsquare.ui.tab.injection.TabComponent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabActivity extends BaseActivity<TabContract.View, TabContract.Presenter> implements TabContract.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

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
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.str_musicians_list);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener((view) -> finish());
    }

    /* Contract */
    // ------------------------------------------
    @Override
    public void showTabs(List<String[]> titles) {
        tabsAdapter.setTabs(titles);
        tabsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {

    }

    @Override
    public void showLoading() {

    }
}
