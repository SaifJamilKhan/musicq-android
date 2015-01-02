package com.haystack.saifkhan.haystack.uI;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haystack.saifkhan.haystack.Adapters.SongListViewAdapter;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.DatabaseManager;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by SaifKhan on 2014-11-08.
 */
public class YoutubePlayerFragment extends Fragment {

    private ViewHolder mHolder;
    private MusicQPlayList mPlaylist;

    private SongListViewAdapter mSongAdapter;
    private QueuePlayControlsListener mPlayControlsListener;

    public void scrollToBottom() {
        mHolder.songListView.setSelection(mSongAdapter.getCount()-1);
    }

    public static interface QueuePlayControlsListener {
        public void didPressPlay(MusicQSong song);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube_play, container, false);
        mSongAdapter = new SongListViewAdapter(getActivity().getLayoutInflater(), getActivity(), false);
        mHolder = new ViewHolder(rootView);
        mHolder.emptyTextView.setText("SWIPE RIGHT TO ADD SONGS AND PULL DOWN TO REFRESH");
        mHolder.songListView.setAdapter(mSongAdapter);
        mHolder.songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                indicateDidPressPlay(i);
                mSongAdapter.setCurrentQueItem(i);
                mSongAdapter.notifyDataSetChanged();
            }
        });

        mHolder.swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3,
                R.color.refresh_progress_4);

        mHolder.swipeRefreshLayout.setListView(mHolder.songListView);
        mHolder.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startSpinner();
                syncCurrentPlaylist();
            }
        });

//        mHolder.songListView.setEmptyView(mHolder.emptyTextView);
//        YoutubeTask task = new YoutubeTask();
//        task.execute(getActivity());


        return rootView;
    }

    private void indicateDidPressPlay(int i) {
        if(mPlayControlsListener == null && getActivity() != null) {
            if(getActivity() instanceof QueuePlayControlsListener) {
                mPlayControlsListener = (QueuePlayControlsListener) getActivity();
            }
        }
        if(mPlayControlsListener != null) {
            mPlayControlsListener.didPressPlay((MusicQSong) mSongAdapter.getItem(i));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startSpinner();
        syncCurrentPlaylist();
        mHolder.emptyTextView.setVisibility(mHolder.songListView.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void syncCurrentPlaylist() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        String currentPlaylistID = sharedPreferences.getString("currentPlaylistID", "");
        loadCurrentSong(currentPlaylistID);

        NetworkUtils.showPlaylist(currentPlaylistID, new NetworkUtils.NetworkCallListener() {
            @Override
            public void didSucceed() {
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopSpinner();
                            mHolder.swipeRefreshLayout.setRefreshing(false);
                            mHolder.emptyTextView.setVisibility(mHolder.songListView.getCount() == 0 ? View.VISIBLE : View.GONE);
                        }
                    });
                }
            }

            @Override
            public void didSucceedWithJson(JSONObject body) {
                Gson gson = new Gson();
                try {
                    mPlaylist = gson.fromJson(body.getJSONObject("playlist").toString(), MusicQPlayList.class);
                    mSongAdapter.setSongs(mPlaylist.songs);
                    Activity activity = getActivity();
                    if(activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSongAdapter.notifyDataSetChanged();
                                mHolder.swipeRefreshLayout.setRefreshing(false);
                                stopSpinner();
                                mHolder.emptyTextView.setVisibility(mHolder.songListView.getCount() == 0 ? View.VISIBLE : View.GONE);
                            }
                        });
                    }
                    DatabaseManager.getDatabaseManager().addObject(mPlaylist);
                    Timber.v("loaded playlist with id " + mPlaylist.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didFailWithMessage(final String message) {
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHolder.swipeRefreshLayout.setRefreshing(false);
                            stopSpinner();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            mHolder.emptyTextView.setVisibility(mHolder.songListView.getCount() == 0 ? View.VISIBLE : View.GONE);

                        }
                    });
                }
            }
        }, getActivity());
    }

    private void loadCurrentSong(String playlistID) {
        HashMap playlists = DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class);
        if(playlists != null && playlists.size() > 0) {
            MusicQPlayList playlist = (MusicQPlayList) playlists.get(playlistID);
            if(playlist != null) {
                mPlaylist = playlist;
                mSongAdapter.setSongs(mPlaylist.songs);
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSongAdapter.notifyDataSetChanged();
                            stopSpinner();
                            mHolder.emptyTextView.setVisibility(mHolder.songListView.getCount() == 0 ? View.VISIBLE : View.GONE);
                        }
                    });
                }
            }
        }
    }

    public void setPlayControlsListener(QueuePlayControlsListener listener) {
        mPlayControlsListener = listener;
    }

    protected class YoutubeTask extends AsyncTask<Context, Integer, ArrayList> {
        @Override
        protected ArrayList doInBackground(Context... params) {
            // Do the time comsuming task here
            ArrayList response;
            try {
                String url = Uri.decode("https%3A%2F%2Fr6---sn-vgpvoj5-tu1e.googlevideo.com%2Fvideoplayback%3Fitag%3D247%26sver%3D3%26gir%3Dyes%26mm%3D31%26ipbits%3D0%26dur%3D223.890%26id%3Do-AHDSwmzIPXO59WFLvYa3SL5indbg30FXcnToHoI08aKk%26keepalive%3Dyes%26lmt%3D1395712736271889%26ms%3Dau%26mv%3Dm%26mt%3D1415491356%26key%3Dyt5%26ip%3D142.1.50.76%26source%3Dyoutube%26mime%3Dvideo%252Fwebm%26clen%3D19600759%26expire%3D1415513039%26upn%3DT9EYaEgtOTY%26requiressl%3Dyes%26sparams%3Dclen%252Cdur%252Cgir%252Cid%252Cinitcwndbps%252Cip%252Cipbits%252Citag%252Ckeepalive%252Clmt%252Cmime%252Cmm%252Cms%252Cmv%252Crequiressl%252Csource%252Cupn%252Cexpire%26signature%3D7C5D60E14C146B36447260E478F3F800528D09C.47DB95E46C90BF9F1246185742FBE146B146D0F%26fexp%3D3300119%252C3300119%252C3300133%252C3300133%252C3300137%252C3300137%252C3300161%252C3300161%252C3310709%252C3310709%252C3312478%252C3312478%252C907259%252C916942%252C922243%252C930666%252C932404%252C934945%252C936119%252C938688%252C941564%252C942702%252C947209%252C947215%252C948122%252C948124%252C952302%252C952605%252C952901%252C953912%252C954202%252C957103%252C957105%252C957201%26initcwndbps%3D5972500\\u0026init=0-234\\u0026fps=1\\u0026type=video%2Fwebm%3B+codecs%3D%22vp9%22\\u0026lmt=1395712736271889\\u0026clen=19600759\\u0026bitrate=1073697,size=854x480\\u0026index=713-1284\\u0026itag=135\\u0026");
//                %3Ffexp%3D900225%252C907259%252C912109%252C915516%252C916633%2" +
//                        "2C924227%252C930666%252C932404%252C947209%252C947215%252C948124%252C952302%252C952605%252C952901%252C953912%252C955202%252C957103%252C957105%2" +
//                        "52C957201%26lmt%3D1394298641936374%26key%3Dyt5%26ip%3D24.114.79.180%26ms%3Dau%26mv%3Dm%26mime%3Dvideo%252Fmp4%26source%3Dyoutube%26dur%3D290.00" +
//                        "1%26id%3Do-AHuhoJ19gRNlG3j-1X8WAHYoGNx9ExtM36CQp4SIaVHv%26upn%3Dpm_IJu8_zMw%26expire%3D1415513194%26mm%3D31%26mt%3D1415491489%26initcwndbps%3D22" +
//                        "20000%26gcr%3Dca%26itag%3D137%26keepalive%3Dyes%26sver%3D3%26clen%3D75491673%26gir%3Dyes%26ipbits%3D0%26sparams%3Dclen%252Cdur%252Cgcr%252Cgir%" +
//                        "252Cid%252Cinitcwndbps%252Cip%252Cipbits%252Citag%252Ckeepalive%252Clmt%252Cmime%252Cmm%252Cms%252Cmv%252Csource%252Cupn%252Cexpire\\u0026init=0-" +
//                        "712\\u0026fps=24\\u0026size=1920x1080,bitrate=2485836\\u0026lmt=1411500971562077\\u0026s=A36112B29F54D4271043CC521489DF61699D172D.86959C2C976150F0" +
//                        "600C8C6C219009397E5AF20F0FF\\u0026type=video%2Fwebm%3B+codecs%3D%22vp9%22\\u0026itag=248\\u0026index=235-1247\\u0026clen=44181946\\u0026");
//                url = "www.google.com";
                new YouTubePageStreamUriGetter().execute("https://www.youtube.com/watch?v=N9ZPfjYYKAc");

                getMP4(url);
                return null;
            } catch (Exception e) {
                response = null;
                e.printStackTrace();
            }

            return null;
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
                    Toast.makeText(getActivity(), "got it", Toast.LENGTH_LONG).show();
                }
            });
            // Show the toast message here
        }
    }

    private void getMP4(String url){
        URL u = null;
        InputStream is = null;

        try {
            u = new URL(url);
            is = u.openStream();
            HttpURLConnection huc = (HttpURLConnection)u.openConnection();//to know the size of video
            int size = huc.getContentLength();
            Log.v("saif", "saif size is " + size);

            if(huc != null){
                String fileName = "FILE.mp4";
                String storagePath = Environment.getExternalStorageDirectory().toString();
                File f = new File(storagePath,fileName);

                FileOutputStream fos = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                if(is != null){
                    while ((len1 = is.read(buffer)) > 0) {
                        fos.write(buffer,0, len1);
                    }
                }
                if(fos != null){
                    fos.close();
                }
            }
        }catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
                try {
                if(is != null){
                    is.close();
                    Log.v("saif", "saif closed");
                }
            }catch (IOException ioe) {
                // just going to ignore this one
            }
        }

    }

//    http://stackoverflow.com/questions/15240011/get-the-download-url-for-youtube-video-android-java?lq=1
    class Meta {
        public String num;
        public String type;
        public String ext;

        Meta(String num, String ext, String type) {
            this.num = num;
            this.ext = ext;
            this.type = type;
        }
    }

    class Video {
        public String ext = "";
        public String type = "";
        public String url = "";

        Video(String ext, String type, String url) {
            this.ext = ext;
            this.type = type;
            this.url = url;
        }
    }

    public ArrayList<Video> getStreamingUrisFromYouTubePage(String ytUrl)
            throws IOException {
        if (ytUrl == null) {
            return null;
        }

        // Remove any query params in query string after the watch?v=<vid> in
        // e.g.
        // http://www.youtube.com/watch?v=0RUPACpf8Vs&feature=youtube_gdata_player
        int andIdx = ytUrl.indexOf('&');
        if (andIdx >= 0) {
            ytUrl = ytUrl.substring(0, andIdx);
        }

        // Get the HTML response
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1)";
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                userAgent);
        HttpGet request = new HttpGet(ytUrl);
        HttpResponse response = client.execute(request);
        String html = "";
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            str.append(line.replace("\\u0026", "&"));
        }
        in.close();
        html = str.toString();

        // Parse the HTML response and extract the streaming URIs
        if (html.contains("verify-age-thumb")) {
            Log.v("saif", "youtube is asking for age verification. We can't handle that sorry.");
            return null;
        }

        if (html.contains("das_captcha")) {
            Log.v("saif", "youtube Captcha found, please try with different IP address.");
            return null;
        }

        Pattern p = Pattern.compile("stream_map\": \"(.*?)?\"");
        // Pattern p = Pattern.compile("/stream_map=(.[^&]*?)\"/");
        Matcher m = p.matcher(html);
        List<String> matches = new ArrayList<String>();
        while (m.find()) {
            matches.add(m.group());
        }

        if (matches.size() != 1) {
            Log.v("saif", "youtube Found zero or too many stream maps.");
            return null;
        }

        String urls[] = matches.get(0).split(",");
        HashMap<String, String> foundArray = new HashMap<String, String>();
        for (String ppUrl : urls) {
            String url = URLDecoder.decode(ppUrl, "UTF-8");

            Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
            Matcher m1 = p1.matcher(url);
            String itag = null;
            if (m1.find()) {
                itag = m1.group(1);
            }

            Pattern p2 = Pattern.compile("signature=(.*?)[&]");
            Matcher m2 = p2.matcher(url);
            String sig = null;
            if (m2.find()) {
                sig = m2.group(1);
            } else {
                Pattern p3 = Pattern.compile("sig=(.*?)[&]");
                Matcher m3 = p3.matcher(url);
                if(m3.find()) {
                    sig = m3.group(1);
                }
            }

            Pattern p3 = Pattern.compile("url=(.*?)[&]");
            Matcher m3 = p3.matcher(ppUrl);
            String um = null;
            if (m3.find()) {
                um = m3.group(1);
            }

            if (itag != null && sig != null && um != null) {
                foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&"
                        + "signature=" + sig);
            }
        }

        if (foundArray.size() == 0) {
            Log.v("saif", "youtube Couldn't find any URLs and corresponding signatures");
            return null;
        }

        HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
        typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
        typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
        typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
        typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
        typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
        typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
        typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
        typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
        typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
        typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
        typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
        typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
        typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
        typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

        ArrayList<Video> videos = new ArrayList();

        for (String format : typeMap.keySet()) {
            Meta meta = typeMap.get(format);

            if (foundArray.containsKey(format)) {
                Video newVideo = new Video(meta.ext, meta.type,
                        foundArray.get(format));
                videos.add(newVideo);
                Log.v("youtube", "youTube Video streaming details: ext:" + newVideo.ext
                        + ", type:" + newVideo.type + ", url:" + newVideo.url);
            }
        }

        return videos;
    }

    private class YouTubePageStreamUriGetter extends
            AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "",
                    "Connecting to YouTube...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            try {
                ArrayList<Video> videos = getStreamingUrisFromYouTubePage(url);
                if (videos != null && !videos.isEmpty()) {
                    String retVidUrl = null;
                    for (Video video : videos) {
                        if (video.ext.toLowerCase().contains("mp4")
                                && video.type.toLowerCase().contains("medium")) {
                            retVidUrl = video.url;
                            break;
                        }
                    }
                    if (retVidUrl == null) {
                        for (Video video : videos) {
                            if (video.ext.toLowerCase().contains("3gp")
                                    && video.type.toLowerCase().contains(
                                    "medium")) {
                                retVidUrl = video.url;
                                break;

                            }
                        }
                    }
                    if (retVidUrl == null) {

                        for (Video video : videos) {
                            if (video.ext.toLowerCase().contains("mp4")
                                    && video.type.toLowerCase().contains("low")) {
                                retVidUrl = video.url;
                                break;

                            }
                        }
                    }
                    if (retVidUrl == null) {
                        for (Video video : videos) {
                            if (video.ext.toLowerCase().contains("3gp")
                                    && video.type.toLowerCase().contains("low")) {
                                retVidUrl = video.url;
                                break;
                            }
                        }
                    }
                    getMP4(retVidUrl);
                    return retVidUrl;
                }
            } catch (Exception e) {
                Log.v("youtube", "youtube Couldn't get YouTube streaming URL", e);
            }
            Log.v("youtube", "Couldn't get stream URI for " + url);
            return null;
        }

        @Override
        protected void onPostExecute(String streamingUrl) {
            super.onPostExecute(streamingUrl);
            progressDialog.dismiss();
            if (streamingUrl != null) {
                Toast.makeText(getActivity(), "Got streming url" + streamingUrl, Toast.LENGTH_LONG).show();
                         /* Do what ever you want with streamUrl */
            }
        }
    }

    private void startSpinner() {
        mHolder.loadingSpinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fast_rotator);
        mHolder.loadingSpinnerImageView.startAnimation(animation);
    }

    private void stopSpinner() {
        mHolder.loadingSpinner.setVisibility(View.GONE);
        mHolder.loadingSpinnerImageView.clearAnimation();
    }

    static class ViewHolder {
        @InjectView(R.id.loading_spinner)
        View loadingSpinner;

        @InjectView(R.id.loading_spinner_image_view)
        View loadingSpinnerImageView;

        @InjectView(R.id.emptyTextView)
        public TextView emptyTextView;

        @InjectView(R.id.songs_list_view)
        ListView songListView;

        @InjectView(R.id.swipe_refresh)
        ListviewSwipeRefreshLayout swipeRefreshLayout;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

