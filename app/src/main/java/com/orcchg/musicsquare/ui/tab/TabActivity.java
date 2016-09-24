package com.orcchg.musicsquare.ui.tab;

import android.animation.ArgbEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
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
import com.orcchg.musicsquare.ui.util.ViewUtils;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabActivity extends BaseActivity<TabContract.View, TabContract.Presenter> implements TabContract.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.loading_view) View loadingView;
    @BindView(R.id.error_view) View errorView;
    @BindView(R.id.rl_toolbar_dropshadow) View dropshadowView;

    @OnClick(R.id.btn_retry)
    public void onRetryClick() {
        presenter.retry();
    }

    private TabComponent tabComponent;
    private TabAdapter tabsAdapter;

    static class Memento {
        private static final String BUNDLE_KEY_CURRENT_PAGE = "bundle_key_current_page";
        private static final String BUNDLE_KEY_FIRST_TIME_SELECT = "bundle_key_first_time_select";

        int currentPage;
        boolean firstTimeSelect = true;

        void toBundle(@NonNull Bundle outState) {
            outState.putInt(BUNDLE_KEY_CURRENT_PAGE, currentPage);
            outState.putBoolean(BUNDLE_KEY_FIRST_TIME_SELECT, firstTimeSelect);
        }

        static Memento fromBundle(@NonNull Bundle savedInstanceState) {
            Memento memento = new Memento();
            memento.currentPage = savedInstanceState.getInt(BUNDLE_KEY_CURRENT_PAGE);
            memento.firstTimeSelect = savedInstanceState.getBoolean(BUNDLE_KEY_FIRST_TIME_SELECT);
            return memento;
        }
    }

    Memento memento = new Memento();
    @ColorInt int[] colorsPrimary, colorsPrimaryDark, colorsAccent;

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
        if (savedInstanceState != null) {
            memento = Memento.fromBundle(savedInstanceState);
        }

        setContentView(R.layout.activity_tabs);
        ButterKnife.bind(this);
        initResources();
        initView();
        initToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.loadGenres();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        memento.toBundle(outState);
        super.onSaveInstanceState(outState);
    }

    /* View */
    // ------------------------------------------
    private void initResources() {
        TypedArray ta1 = getResources().obtainTypedArray(R.array.colorsPrimary);
        TypedArray ta2 = getResources().obtainTypedArray(R.array.colorsPrimaryDark);
        TypedArray ta3 = getResources().obtainTypedArray(R.array.colorsAccent);
        colorsPrimary = new int[ta1.length() + 1];
        colorsPrimaryDark = new int[ta2.length() + 1];
        colorsAccent = new int[ta3.length() + 1];
        for (int i = 0; i < ta1.length(); ++i) {
            colorsPrimary[i] = ta1.getColor(i, 0);
            colorsPrimaryDark[i] = ta2.getColor(i, 0);
            colorsAccent[i] = ta3.getColor(i, 0);
        }
        colorsPrimary[ta1.length()] = ViewUtils.getAttributeColor(this, R.attr.colorPrimary);
        colorsPrimaryDark[ta2.length()] = ViewUtils.getAttributeColor(this, R.attr.colorPrimaryDark);
        colorsAccent[ta3.length()] = ViewUtils.getAttributeColor(this, R.attr.colorAccent);
        ta1.recycle();
        ta2.recycle();
        ta3.recycle();
    }

    private void initView() {
        tabsAdapter = new TabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                animateBars();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateBars();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initToolbar() {
        toolbar.setNavigationOnClickListener((view) -> finish());
    }

    ValueAnimator.AnimatorUpdateListener createToolbarAnimatorUpdateListener() {
        return (animation) -> {
            @ColorInt int color = (int) animation.getAnimatedValue();
            toolbar.setBackgroundColor(color);
            tabLayout.setBackgroundColor(color);
        };
    }

    ValueAnimator.AnimatorUpdateListener createStatusbarAnimatorUpdateListener() {
        return (animation) -> {
            @ColorInt int color = (int) animation.getAnimatedValue();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(color);
            }
        };
    }

    ValueAnimator.AnimatorUpdateListener createIndicatorAnimatorUpdateListener() {
        return (animation) -> {
            @ColorInt int color = (int) animation.getAnimatedValue();
            tabLayout.setSelectedTabIndicatorColor(color);
        };
    }

    void animateBars() {
        if (memento.firstTimeSelect) {
            memento.firstTimeSelect = false;
            // skip selection for the very first time
            return;
        }

        TypeEvaluator evaluator = new ArgbEvaluator();
        Random random = new Random();
        int fromIndex = random.nextInt(colorsPrimary.length);
        int toIndex = random.nextInt(colorsPrimary.length);
        @ColorInt int fromColor = colorsPrimary[fromIndex];
        @ColorInt int toColor = colorsPrimary[toIndex];
        @ColorInt int fromColorDark = colorsPrimaryDark[fromIndex];
        @ColorInt int toColorDark = colorsPrimaryDark[toIndex];
        @ColorInt int fromAccent = colorsAccent[fromIndex];
        @ColorInt int toAccent = colorsAccent[toIndex];
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, fromColor, toColor);
        ValueAnimator animatorDark = ValueAnimator.ofObject(evaluator, fromColorDark, toColorDark);
        ValueAnimator animatorAccent = ValueAnimator.ofObject(evaluator, fromAccent, toAccent);
        animator.addUpdateListener(createToolbarAnimatorUpdateListener());
        animatorDark.addUpdateListener(createStatusbarAnimatorUpdateListener());
        animatorAccent.addUpdateListener(createIndicatorAnimatorUpdateListener());
        animator.setDuration(350);
        animatorDark.setDuration(350);
        animatorAccent.setDuration(350);
        animator.start();
        animatorDark.start();
        animatorAccent.start();
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

            viewPager.setCurrentItem(memento.currentPage);
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
