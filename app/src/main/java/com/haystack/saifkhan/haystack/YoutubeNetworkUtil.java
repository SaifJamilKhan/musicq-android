package com.haystack.saifkhan.haystack;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class YoutubeNetworkUtil {

    private static String BASE_YOUTUBE_URL_V3 = "https://www.googleapis.com/youtube/v3/";
    private static String YOUTUBE_PLAYER_KEY = "AIzaSyDjwFylfhG-n4cpFITu952IXWFNS6FktpU";
    private static String YOUTUBE_API_KEY = "AIzaSyDTb4VB39ecxU9XXr5l-9C5Nn_d1d6UIpQ";

    private static String SEARCH = "/search";
    private static YouTube youtube;

    public static String searchForVideosByTheName(String name) throws IOException {
        name = name.trim();

        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("com.haystack.saifkhan.haystack").build();


        YouTube.Search.List listVideosRequest = youtube.search().list("snippet");
        listVideosRequest.setKey(YOUTUBE_API_KEY);
        listVideosRequest.setQ(name);
        listVideosRequest.setType("video");
        listVideosRequest.setMaxResults((long) 10);
        SearchListResponse searchResponse = listVideosRequest.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();
        List<String> videoIds = new ArrayList<String>();

        return "GOT " + videoIds.size();
    }
}
