package com.haystack.saifkhan.haystack.Models;

/**
 * Created by SaifKhan on 2014-11-08.
 */
public class MusicQSong {
    private String id;
    private String youtubeID;
    private String snippet;
    private String title;
    private String thumbnailURL;
    private Long thumbnailWidth;
    private Long thumbnailHeight;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYoutubeID() {
        return youtubeID;
    }

    public void setYoutubeID(String youtubeID) {
        this.youtubeID = youtubeID;
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

    public Long getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(Long thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public Long getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(Long thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }
}
