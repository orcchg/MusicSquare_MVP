package com.orcchg.musicsquare.ui.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.navigation.Navigator;
import com.orcchg.musicsquare.util.ViewUtility;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rl_toolbar_dropshadow) View dropshadowView;

    @Inject Navigator navigator;

    public static Intent getCallingIntent(@NonNull Context context) {
        return new Intent(context, ListActivity.class);
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
    
    /* View */
    // ------------------------------------------
    private void initView() {
        String tag = "list-fragment-tag";
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            ListFragment fragment = ListFragment.newInstance();
            fm.beginTransaction().replace(R.id.container, fragment, tag).commit();
            fm.executePendingTransactions();
        }
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
                case R.id.tabs:
                    ListActivity.this.navigator.openTabsScreen(ListActivity.this);
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

    void showShadow(boolean show) {
        dropshadowView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
