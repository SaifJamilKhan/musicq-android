package com.haystack.saifkhan.haystack.uI;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

import com.google.gson.Gson;
import com.haystack.saifkhan.haystack.Adapters.SongListViewAdapter;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.DatabaseManager;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
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
    private PlaylistSelectListener playlistSelectListener;

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
                if(playlistSelectListener == null && getActivity() != null) {
                    if(getActivity() instanceof PlaylistSelectListener) {
                        playlistSelectListener = (PlaylistSelectListener) getActivity();
                    }
                }
                if(playlistSelectListener != null) {
                    playlistSelectListener.didSelectPlaylistWithId(((MusicQPlayList) mQueuesAdapter.getItem(i)).id);
                }
            }
        });
        return rootView;
    }

    private void setEmptyView() {
        mHolder.gridView.setEmptyView(mHolder.emptyTextView);
    }

    private void getAllPlaylistsFromNetwork() {
        NetworkUtils.getAllPlaylists(new NetworkUtils.NetworkCallListener() {
            @Override
            public void didSucceed() {
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadPlaylistsFromDatabase();
                            stopSpinner();
                            mHolder.swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }

            @Override
            public void didSucceedWithJson(JSONObject body) {
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadPlaylistsFromDatabase();
                            mHolder.swipeRefreshLayout.setRefreshing(false);
                            stopSpinner();
                        }
                    });
                }
            }

            @Override
            public void didFailWithMessage(String message) {
                Timber.e(message);
                stopSpinner();
                loadPlaylistsFromDatabase();
                mHolder.swipeRefreshLayout.setRefreshing(false);
            }
        }, getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPlaylistsFromDatabase();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopSpinner();
    }

    private void loadPlaylistsFromDatabase() {
        HashMap playlists = DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class);
        if (playlists == null || playlists.size() == 0) {
            SharedPreferences sharedPrefEditor = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
            String jsonString = sharedPrefEditor.getString("mostRecentPlaylists", "");
            if(!TextUtils.isEmpty(jsonString)) {
                Timber.v(jsonString + "saif");
                JSONArray jsonArray;
                try {
                    Gson gson = new Gson();
                    jsonArray = new JSONArray(jsonString);

                    for (int x = 0; x < jsonArray.length(); x++) {
                        JSONObject object = jsonArray.getJSONObject(x);

                        final MusicQPlayList playlist = gson.fromJson(object.toString(), MusicQPlayList.class);
                        if (!TextUtils.isEmpty(playlist.id)) {
                            DatabaseManager.getDatabaseManager().addObject(playlist);
                        } else {
                            Timber.v("");
                        }
                    }
                    playlists = DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Timber.v(e.getMessage() + "saif");
                }
            }
        }
        if ((playlists != null && playlists.size() > 0) && mHolder.gridView.getChildCount() != playlists.size()) {
            mQueuesAdapter.setQueues(new ArrayList<MusicQPlayList>(playlists.values()));
            mQueuesAdapter.notifyDataSetChanged();
        } else {
            startSpinner();
        }
    }

    public void setPlaylistSelectListener(PlaylistSelectListener playlistSelectListener) {
        this.playlistSelectListener = playlistSelectListener;
    }

    private void startSpinner() {
        mHolder.loadingSpinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fast_rotator);
        mHolder.loadingSpinnerImageView.startAnimation(animation);
    }

    private void stopSpinner() {
        mHolder.loadingSpinner.setVisibility(View.GONE);
        mHolder.loadingSpinnerImageView.clearAnimation();
    }

    static class ViewHolder {
        @InjectView(R.id.loading_spinner)
        View loadingSpinner;

        @InjectView(R.id.loading_spinner_image_view)
        View loadingSpinnerImageView;

        @InjectView(R.id.emptyTextView)
        public TextView emptyTextView;

        @InjectView(R.id.grid_view)
        public GridView gridView;

        @InjectView(R.id.swipe_refresh)
        public ListviewSwipeRefreshLayout swipeRefreshLayout;

        public ViewHolder(View rootView) {
            ButterKnife.inject(this, rootView);
        }
    }

    public static interface PlaylistSelectListener {
        public void didSelectPlaylistWithId(String id);
    }
}
