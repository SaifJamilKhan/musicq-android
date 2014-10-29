package com.haystack.saifkhan.haystack;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class YoutubeNetworkUtil {

    public static String searchForVideosByTheName(String name) throws IOException {
        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://gdata.youtube.com/feeds/api/videos?" +
                            "q=" + name +
                            "&orderby=published" +
                            "&start-index=11" +
                            "&max-results=10" +
                            "&v=2")
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
    }
}
