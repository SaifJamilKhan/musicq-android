package com.haystack.saifkhan.haystack.uI;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by saifkhan on 14-12-30.
 */
public class QueueGridAdapter extends BaseAdapter{


    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private ArrayList<MusicQPlayList> mQueues = new ArrayList<MusicQPlayList>();

    public QueueGridAdapter(LayoutInflater layoutInflater, Activity activity) {
        mLayoutInflater = layoutInflater;
        mActivity = activity;
    }

    public void setQueues(ArrayList<MusicQPlayList> queues) {
        mQueues = queues;
    }
    @Override
    public int getCount() {
        return mQueues.size();
    }

    @Override
    public Object getItem(int i) {
        return mQueues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        PlaylistViewHolder  holder;
        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.square_image_view_with_text, parent, false);
            holder = new PlaylistViewHolder (view);
            view.setTag(holder);
        } else {
            holder = (PlaylistViewHolder ) view.getTag();
        }

        MusicQPlayList playlist = (MusicQPlayList) getItem(i);
        holder.titleView.setText(playlist.name);
        String thumbnailURL = "";
        if(playlist.songs != null && playlist.songs.size() > 0) {
            MusicQSong latestSong = playlist.songs.get(playlist.songs.size() -1);
            thumbnailURL = latestSong.thumbnailURL;
        }
        if(!TextUtils.isEmpty(thumbnailURL)) {
            Picasso.with(mActivity).load(thumbnailURL).into(holder.playlistImageView);
        } else {
            holder.playlistImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.logo));
        }
        return view;
    }

    public static class PlaylistViewHolder {
        @InjectView(R.id.title_text_view)
        TextView titleView;

        @InjectView(R.id.image_view)
        ImageView playlistImageView;

        public PlaylistViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
