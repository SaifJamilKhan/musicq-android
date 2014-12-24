package com.haystack.saifkhan.haystack.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by saifkhan on 14-12-14.
 */
public class MusicQPlayList extends MusicQBase{

    public String name;
    public String description;

    @SerializedName("videos")
    public ArrayList<MusicQSong> songs;

//    @SerializedName("updated_at")
//    public DateTime updatedAt;


}
