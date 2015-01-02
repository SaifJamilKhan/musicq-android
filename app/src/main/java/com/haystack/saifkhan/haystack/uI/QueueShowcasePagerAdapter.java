package com.haystack.saifkhan.haystack.uI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by saifkhan on 14-12-30.
 */
public class QueueShowcasePagerAdapter extends FragmentStatePagerAdapter {


    private ListOfQueuesFragment.PlaylistSelectListener playlistSelectListener;

    public QueueShowcasePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public QueueShowcasePagerAdapter(FragmentManager fragmentManager, ListOfQueuesFragment.PlaylistSelectListener playlistSelectListener) {
        super(fragmentManager);
        this.playlistSelectListener = playlistSelectListener;
    }

    @Override
    public Fragment getItem(int i) {
        ListOfQueuesFragment fragment = new ListOfQueuesFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ListOfQueuesFragment.PAGE_NUMBER, i + 1);
        fragment.setArguments(args);

        fragment.setPlaylistSelectListener(playlistSelectListener);
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
