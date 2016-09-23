package com.orcchg.musicsquare.ui.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.orcchg.musicsquare.ui.list.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

    private final List<String[]> tabs;

    public TabAdapter(FragmentManager fm) {
        super(fm);
        this.tabs = new ArrayList<>();
    }

    void setTabs(List<String[]> tabs) {
        this.tabs.clear();
        this.tabs.addAll(tabs);
    }

    @Override
    public Fragment getItem(int position) {
        return ListFragment.newInstance(tabs.get(position));
    }

    @Override
    public int getCount() {
        return this.tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.tabs.isEmpty() ? "" : this.tabs.get(position)[0];
    }
}
