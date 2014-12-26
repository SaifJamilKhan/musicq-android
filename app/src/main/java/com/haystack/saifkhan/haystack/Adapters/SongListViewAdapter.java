package com.haystack.saifkhan.haystack.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by SaifKhan on 2014-11-08.
 */
public class SongListViewAdapter extends BaseAdapter{

    private final Context mContext;
    private final boolean mShouldEnableAdding;
    private ArrayList<MusicQSong> mSongs;
    private LayoutInflater mLayoutInflater;
    private Integer mCurrentPlayingSong;

    public void setSongs(ArrayList<MusicQSong> songs) {
        mSongs = songs;
    }

    public SongListViewAdapter(LayoutInflater inflater, Context context, boolean shouldEnableAdding) {
        mSongs = new ArrayList<MusicQSong>();
        mLayoutInflater = inflater;
        mContext = context;
        mShouldEnableAdding = shouldEnableAdding;
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Object getItem(int i) {
        return mSongs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        SongViewHolder holder;
        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.item_song_cell, parent, false);
            holder = new SongViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (SongViewHolder) view.getTag();
        }

        MusicQSong song = (MusicQSong) getItem(i);
        holder.titleView.setText(song.getTitle());
        holder.descriptionView.setText(song.getSnippet());
        holder.thumbnailImageView.setMinimumHeight(100);
        holder.thumbnailImageView.setMinimumWidth(500);
        holder.addSongTextview.setVisibility(mShouldEnableAdding ? View.VISIBLE : View.GONE);
        if(mCurrentPlayingSong != null) {
            if (i == mCurrentPlayingSong) {
                view.setBackgroundResource(R.color.current_playing_song);
            } else if (i < mCurrentPlayingSong) {
                view.setBackgroundResource(R.color.already_played_song);
            } else {
                view.setBackgroundResource(R.color.upcoming_song);
            }
        }
        Picasso.with(mContext).load(song.getThumbnailURL()).into(holder.thumbnailImageView);
        return view;
    }

    public void setCurrentQueItem(Integer i) {
        mCurrentPlayingSong = i;
    }

    public static class SongViewHolder {
        @InjectView(R.id.song_title)
        TextView titleView;

        @InjectView(R.id.song_description)
        TextView descriptionView;

        @InjectView(R.id.thumbnailImageView)
        ImageView thumbnailImageView;

        @InjectView(R.id.addSongButton)
        TextView addSongTextview;


        public SongViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
