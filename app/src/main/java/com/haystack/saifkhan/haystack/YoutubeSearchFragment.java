package com.haystack.saifkhan.haystack;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.haystack.saifkhan.haystack.Adapters.SongListViewAdapter;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class YoutubeSearchFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ViewHolder mHolder;
    private SongListViewAdapter mSongAdapter;

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
        mHolder.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoutubeTask task = new YoutubeTask();
                task.execute(getActivity());


            }
        });
        mSongAdapter = new SongListViewAdapter(getActivity().getLayoutInflater(), getActivity());
        mHolder.youtubeListView.setAdapter(mSongAdapter);
        return rootView;
    }

    protected class YoutubeTask extends AsyncTask<Context, Integer, ArrayList> {
        @Override
        protected ArrayList doInBackground(Context... params) {
            // Do the time comsuming task here
            ArrayList response;
            try {
                response = (ArrayList) YoutubeNetworkUtil.searchForVideosByTheName(mHolder.searchBar.getText().toString());
                return response;
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

        // -- called from the publish progress
        // -- notice that the datatype of the second param gets passed to this
        // method
        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        // -- called if the cancel button is pressed
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        // -- called as soon as doInBackground method completes
        // -- notice that the third param gets passed to this method
        @Override
        protected void onPostExecute(final ArrayList result) {
            super.onPostExecute(result);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    setSongs(result);
                    Toast.makeText(getActivity(), "got it", Toast.LENGTH_LONG).show();
                }
            });
            // Show the toast message here
        }
    }

    private void setSongs(ArrayList result) {
        if(result != null && mSongAdapter != null) {
            mSongAdapter.setSongs(result);
            mSongAdapter.notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        @InjectView(R.id.search_bar)
        EditText searchBar;
        @InjectView(R.id.youtube_search_btn)
        Button searchBtn;
        @InjectView(R.id.youtube_search_results_list_view)
        ListView youtubeListView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
