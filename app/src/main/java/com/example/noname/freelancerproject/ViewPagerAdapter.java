package com.example.noname.freelancerproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Smart_PC on 6/12/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragment;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragment) {
        super(fm);
        this.fragment = fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment.get(position);
    }

    @Override
    public int getCount() {
        return fragment.size();
    }
}