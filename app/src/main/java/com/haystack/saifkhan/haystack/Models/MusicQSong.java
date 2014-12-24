package com.haystack.saifkhan.haystack.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SaifKhan on 2014-11-08.
 */
public class MusicQSong extends MusicQBase {

    @SerializedName("id")
    public String id;

    @SerializedName("playlist_id")
    public String playlistID;

    @SerializedName("name")
    public String title;

    @SerializedName("description")
    public String snippet;

    @SerializedName("source_id")
    public String sourceID;

    @SerializedName("video_type")
    public String videoType;

    @SerializedName("thumbnail_url")
    public String thumbnailURL;

    @SerializedName("thumbnail_width")
    public Integer thumbnailWidth;

    @SerializedName("thumbnail_height")
    public Integer thumbnailHeight;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(Integer thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public Integer getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(Integer thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }
}
