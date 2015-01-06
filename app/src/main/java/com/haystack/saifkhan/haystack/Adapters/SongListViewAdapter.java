package com.haystack.saifkhan.haystack.Adapters;

import android.content.Context;
import android.graphics.Color;
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
        if(mSongs == null) {
            mSongs = new ArrayList<MusicQSong>();
        }
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
                    view.setBackground(mContext.getResources().getDrawable(R.drawable.red_border));
                    holder.titleView.setTextColor(Color.parseColor("#FC2052"));
                    holder.descriptionView.setTextColor(Color.parseColor("#888888"));
            } else{
                view.setBackground(null);
                holder.titleView.setTextColor(Color.WHITE);
                holder.descriptionView.setTextColor(Color.DKGRAY);
            }
        } else {
            view.setBackground(null);
            holder.titleView.setTextColor(Color.WHITE);
            holder.descriptionView.setTextColor(Color.DKGRAY);
        }
        Picasso.with(mContext).load(song.getThumbnailURL()).into(holder.thumbnailImageView);
        return view;
    }

    public void setCurrentQueItem(Integer i) {
        mCurrentPlayingSong = i;
    }

    public int getCurrentQueItem() {
        return mCurrentPlayingSong;
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
