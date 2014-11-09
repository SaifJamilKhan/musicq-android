package com.haystack.saifkhan.haystack;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SaifKhan on 2014-11-08.
 */
public class YoutubePlayerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_youtube_search, container, false);
//        mHolder = new ViewHolder(rootView);
//        mHolder.searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                YoutubeTask task = new YoutubeTask();
//                task.execute(getActivity());
//
//
//            }
//        });
//        if(mSongAdapter == null) {
//            mSongAdapter = new SongListViewAdapter(getActivity().getLayoutInflater(), getActivity());
//        }
//        mHolder.youtubeListView.setAdapter(mSongAdapter);
        String url = Uri.decode("http%3A%2F%2Fr4---sn-gvbxgn-tt1l.googlevideo.com%2Fvideoplayback%3Ffexp%3D900225%252C907259%252C912109%252C915516%252C916633%2" +
                "2C924227%252C930666%252C932404%252C947209%252C947215%252C948124%252C952302%252C952605%252C952901%252C953912%252C955202%252C957103%252C957105%252C957201%26lmt%3D1394298641936374%26ke" +
                "y%3Dyt5%26ip%3D24.114.79.180%26ms%3Dau%26mv%3Dm%26mime%3Dvideo%252Fmp4%26source%3Dyoutube%26dur%3D290.001%26id%3Do-AHuhoJ19gRNlG3j-1X8WAHYoGN" +
                "x9ExtM36CQp4SIaVHv%26upn%3Dpm_IJu8_zMw%26expire%3D1415513194%26mm%3D31%26mt%3D1415491489%26initcwndbps%3D2220000%26gcr%3Dca%26itag%3D137%26keepalive%3Dyes%26sver%3D3%26" +
                "clen%3D75491673%26gir%3Dyes%26ipbits%3D0%26sparams%3Dclen%252Cdur%252Cgcr%252Cgir%252Cid%252Cinitcwndbps%252Cip%252Cipbits%252Citag%252Ckeepalive%252Clmt%252Cmime%252Cmm%252Cms%252Cmv%252Csource%252Cupn%252C");

        getMP4(url);
        return container;
    }

    private void getMP4(String url){
        URL u = null;
        InputStream is = null;

        try {
            u = new URL(url);
            is = u.openStream();
            HttpURLConnection huc = (HttpURLConnection)u.openConnection();//to know the size of video
            int size = huc.getContentLength();

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
                }
            }catch (IOException ioe) {
                // just going to ignore this one
            }
        }
    }
}

