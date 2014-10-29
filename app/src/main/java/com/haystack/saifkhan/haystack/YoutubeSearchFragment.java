package com.haystack.saifkhan.haystack;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class YoutubeSearchFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ViewHolder mHolder;

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
        return rootView;
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
