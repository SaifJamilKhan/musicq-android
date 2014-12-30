package com.haystack.saifkhan.haystack.uI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by saifkhan on 14-12-30.
 */
public class QueueShowcasePagerAdapter extends FragmentStatePagerAdapter {


    public QueueShowcasePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ListOfQueuesFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ListOfQueuesFragment.PAGE_NUMBER, i + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0) ? "YOURS" : "TRENDING";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
