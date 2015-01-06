package com.haystack.saifkhan.haystack.uI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.YoutubeNetworkUtil;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity implements YoutubePlayerFragment.QueuePlayControlsListener, YoutubeSearchFragment.AddSongListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    @InjectView(R.id.pager)
    ViewPager mViewPager;
    private YouTubePlayerFragment youtubeFragment;
    private YouTubePlayer mYoutubePlayer;
    private YoutubePlayerFragment mYoutubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setTitle("Playlist ID: " + getPlaylistId());
        youtubeFragment =  (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        youtubeFragment.initialize(YoutubeNetworkUtil.YOUTUBE_PLAYER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
               mYoutubePlayer = youTubePlayer;
               mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
               mYoutubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                   @Override
                   public void onLoading() {

                   }

                   @Override
                   public void onLoaded(String s) {

                   }

                   @Override
                   public void onAdStarted() {

                   }

                   @Override
                   public void onVideoStarted() {

                   }

                   @Override
                   public void onVideoEnded() {
                       mYoutubePlayerFragment.playNextVideo();
                   }

                   @Override
                   public void onError(YouTubePlayer.ErrorReason errorReason) {

                   }
               });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private String getPlaylistId() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentPlaylistID", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Toast.makeText(this, "TODO: Add Logout", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void didPressPlay(MusicQSong song) {
        if(mYoutubePlayer != null) {
            mYoutubePlayer.loadVideo(song.getSourceID());
        }
    }

    @Override
    public void didPressAddButton() {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void didAddSong() {
        mViewPager.setCurrentItem(0);
        mYoutubePlayerFragment.scrollToBottom();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0) {
                if(mYoutubePlayerFragment == null) {
                    mYoutubePlayerFragment = new YoutubePlayerFragment();
                    mYoutubePlayerFragment.setPlayControlsListener(MainActivity.this);
                }
                return mYoutubePlayerFragment;
            }
            else if(position == 1) {
                return YoutubeSearchFragment.newInstance(2);
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
