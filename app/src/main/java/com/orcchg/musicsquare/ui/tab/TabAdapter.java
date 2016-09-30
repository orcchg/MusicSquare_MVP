package com.orcchg.musicsquare.ui.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.domain.model.Genre;
import com.orcchg.musicsquare.ui.list.ListFragment;

import java.util.ArrayList;
import java.util.List;

class TabAdapter extends FragmentStatePagerAdapter {

    private final List<Genre> tabs;

    TabAdapter(FragmentManager fm) {
        super(fm);
        tabs = new ArrayList<>();
    }

    void setTabs(List<Genre> genres) {
        tabs.clear();
        tabs.addAll(genres);
    }

    @Override
    public Fragment getItem(int position) {
        return ListFragment.newInstance((ArrayList<String>) tabs.get(position).getGenres());
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.isEmpty() ? "" : tabs.get(position).getName();
    }
}
