package com.haystack.saifkhan.haystack.uI;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    public static final String SELECT_LISTENER = "SELECTED_LISTENER";
    private ViewHolder mHolder;
    private QueueGridAdapter mQueuesAdapter;
    private EnterRoomActivity.PlaylistSelectListener playlistSelectListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grid_view, container, false);
        mHolder = new ViewHolder(rootView);
        mQueuesAdapter = new QueueGridAdapter(getActivity().getLayoutInflater(), getActivity());
        mHolder.gridView.setAdapter(mQueuesAdapter);
        setEmptyView();
        getAllPlaylistsFromNetwork();
        mHolder.swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3,
                R.color.refresh_progress_4);

        mHolder.swipeRefreshLayout.setListView(mHolder.gridView);
        mHolder.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPlaylistsFromNetwork();
            }
        });
        mHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(playlistSelectListener != null) {
                    playlistSelectListener.didSelectPlaylistWithId(((MusicQPlayList) mQueuesAdapter.getItem(i)).id);
                }
            }
        });
        return rootView;
    }

    private void setEmptyView() {
        TextView emptyTextView = new TextView(getActivity());
        emptyTextView.setText("Playlist You");


    }

    private void getAllPlaylistsFromNetwork() {
        NetworkUtils.getAllPlaylists(new NetworkUtils.NetworkCallListener() {
            @Override
            public void didSucceed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylistsFromDatabase();
                        mHolder.swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void didSucceedWithJson(JSONObject body) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylistsFromDatabase();
                        mHolder.swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void didFailWithMessage(String message) {
                Timber.e(message);
            }
        }, getActivity());
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

    public void setPlaylistSelectListener(EnterRoomActivity.PlaylistSelectListener playlistSelectListener) {
        this.playlistSelectListener = playlistSelectListener;
    }

    static class ViewHolder {

        @InjectView(R.id.grid_view)
        public GridView gridView;

        @InjectView(R.id.loading_spinner)
        public View spinnerView;

        @InjectView(R.id.loading_spinner_image_view)
        public ImageView spinnerImageView;

        @InjectView(R.id.swipe_refresh)
        public ListviewSwipeRefreshLayout swipeRefreshLayout;

        public ViewHolder(View rootView) {
            ButterKnife.inject(this, rootView);
        }
    }
}
