package com.haystack.saifkhan.haystack.uI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haystack.saifkhan.haystack.Adapters.SongListViewAdapter;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.DatabaseManager;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;
import com.haystack.saifkhan.haystack.Utils.ViewUtils;
import com.haystack.saifkhan.haystack.Utils.YoutubeNetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class YoutubeSearchFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ViewHolder mHolder;
    private SongListViewAdapter mSongAdapter;
    private Animation mLeftSwipeAnimation;
    private YoutubeTask mYoutubeTask;

    public static interface AddSongListener {
        public void didAddSong();
    }
    public static YoutubeSearchFragment newInstance(int sectionNumber) {
        YoutubeSearchFragment fragment = new YoutubeSearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public YoutubeSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube_search, container, false);
        mHolder = new ViewHolder(rootView);
        mHolder.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(mYoutubeTask == null) {
                        mYoutubeTask = new YoutubeTask();
                    } else {
                        mYoutubeTask.cancel(true);
                        mYoutubeTask = new YoutubeTask();
                    }
                    mYoutubeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    ViewUtils.hideKeyboardFromTextview(v, getActivity());
                    startSpinner();
                    return true;
                }
                return false;
            }
        });
        if(mSongAdapter == null) {
            mSongAdapter = new SongListViewAdapter(getActivity().getLayoutInflater(), getActivity(), true);
        }
        mHolder.youtubeListView.setAdapter(mSongAdapter);
        mHolder.youtubeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mLeftSwipeAnimation != null && !mLeftSwipeAnimation.hasEnded()) {
                    return;
                }
                MusicQSong song = (MusicQSong) mSongAdapter.getItem(i);
                song.id = "" + Integer.MAX_VALUE;
                addSong(song);
                View cellView = mHolder.youtubeListView.getChildAt(i - mHolder.youtubeListView.getFirstVisiblePosition());
                if (cellView != null) {
                    Activity activity = getActivity();
                    if (activity != null) {
                        mLeftSwipeAnimation = AnimationUtils.loadAnimation(activity, R.anim.swipe_left);
                        cellView.startAnimation(mLeftSwipeAnimation);
                        mLeftSwipeAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                Activity activity = YoutubeSearchFragment.this.getActivity();
                                if (activity instanceof AddSongListener) {
                                    ((AddSongListener) activity).didAddSong();
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            }
        });
        return rootView;
    }

    private void addSong(MusicQSong song) {
        NetworkUtils.createVideo(song, new NetworkUtils.NetworkCallListener() {
            @Override
            public void didSucceed() {

            }

            @Override
            public void didSucceedWithJson(JSONObject body) {

                Gson gson = new Gson();
                try {
                    final MusicQSong musicQSong = gson.fromJson(body.getJSONObject("video").toString(), MusicQSong.class);
                    if(!TextUtils.isEmpty(musicQSong.id) && !TextUtils.isEmpty(musicQSong.playlistID)) {
                        MusicQPlayList playList = (MusicQPlayList) DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class).get(musicQSong.playlistID);
                        if(playList != null && (playList.songs == null || playList.songs.size() == 0 || !playList.songs.get(playList.songs.size() -1).id.equals(musicQSong.getId()))) {
                            if(playList.songs == null) {
                                playList.songs = new ArrayList<MusicQSong>();
                            }
                            playList.songs.add(musicQSong);
                        }
                        DatabaseManager.getDatabaseManager().addObject(playList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didFailWithMessage(String message) {

            }
        }, getActivity());
    }

    protected class YoutubeTask extends AsyncTask<Context, Integer, ArrayList> {
        @Override
        protected ArrayList doInBackground(Context... params) {
            ArrayList response = new ArrayList();
            try {
                if(getActivity() != null) {
                    Timber.v("saif timing started search");
                    response = (ArrayList) YoutubeNetworkUtil.searchForVideosByTheName(mHolder.searchBar.getText().toString(), getActivity());
                    return response;
                }
            } catch (IOException e) {
                response = null;
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(final ArrayList result) {
            super.onPostExecute(result);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    stopSpinner();
                    setSongs(result);
                }
            });
        }
    }

    private void setSongs(ArrayList result) {
        if(result != null && mSongAdapter != null) {
            mSongAdapter.setSongs(result);
            mSongAdapter.notifyDataSetChanged();
        }
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

        @InjectView(R.id.search_bar)
        EditText searchBar;
        @InjectView(R.id.youtube_search_results_list_view)
        ListView youtubeListView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
