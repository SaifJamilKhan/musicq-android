package com.haystack.saifkhan.haystack.uI;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.haystack.saifkhan.haystack.Adapters.SongListViewAdapter;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.DatabaseManager;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by saifkhan on 14-12-30.
 */
public class ListOfQueuesFragment extends Fragment {
    public static final String PAGE_NUMBER = "PAGE_NUMBA";
    private ViewHolder mHolder;
    private QueueGridAdapter mQueuesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grid_view, container, false);
        mHolder = new ViewHolder(rootView);
        mQueuesAdapter = new QueueGridAdapter(getActivity().getLayoutInflater(), getActivity());
        mHolder.gridView.setAdapter(mQueuesAdapter);
        NetworkUtils.getAllPlaylists(new NetworkUtils.NetworkCallListener() {
            @Override
            public void didSucceed() {
               getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylistsFromDatabase();
                    }
                });
            }

            @Override
            public void didSucceedWithJson(JSONObject body) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylistsFromDatabase();
                    }
                });
            }

            @Override
            public void didFailWithMessage(String message) {
                Timber.e(message);

            }
        }, getActivity());
//        mHolder.swipeRefreshLayout.setColorSchemeResources(
//                R.color.refresh_progress_1,
//                R.color.refresh_progress_2,
//                R.color.refresh_progress_3,
//                R.color.refresh_progress_4);
//
//        mHolder.swipeRefreshLayout.setListView(mHolder.songListView);
//        mHolder.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                syncCurrentPlaylist();
//            }
//        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPlaylistsFromDatabase();
    }

    private void loadPlaylistsFromDatabase() {
        HashMap<String, MusicQPlayList> playlists = DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class);
        if ((playlists != null && playlists.size() > 0) && mHolder.gridView.getChildCount() != playlists.size()) {
            mQueuesAdapter.setQueues(new ArrayList<MusicQPlayList>(playlists.values()));
            mQueuesAdapter.notifyDataSetChanged();
        }
    }

    static class ViewHolder {

        @InjectView(R.id.grid_view)
        public GridView gridView;

        public ViewHolder(View rootView) {
            ButterKnife.inject(this, rootView);
        }
    }
}
