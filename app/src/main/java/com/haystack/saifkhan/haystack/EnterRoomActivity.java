package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class EnterRoomActivity extends Activity{

    @InjectView(R.id.playlist_name)
    EditText playlistName;

    @InjectView(R.id.playlist_description)
    EditText playlistDescription;

    @InjectView(R.id.existing_playlist_pin)
    EditText existingPlaylistDescription;

    @InjectView(R.id.loading_spinner)
    private View loadingSpinner;

    @InjectView(R.id.loading_spinner_image_view)
    private View loadingSpinnerImageView;

    @OnClick(R.id.existing_room_btn)
    public void onExistingPressed(View view) {
        goToMainActivity();
    }

    @OnClick(R.id.new_room_btn)
    public void onNewRoomPressed(View view) {
        MusicQPlayList playlist = new MusicQPlayList();
        playlist.name = playlistName.getText().toString();
        playlist.description = playlistDescription.getText().toString();
        if(validateCreateCall()) {
            startSpinner();
            NetworkUtils.createPlaylist(playlist, new NetworkUtils.NetworkCallListener() {
                @Override
                public void didSucceed() {

                }

                @Override
                public void didSucceedWithJson(JSONObject body) {
                    Gson gson = new Gson();
                    try {
                        MusicQPlayList playList = gson.fromJson(body.getJSONObject("data").getJSONObject("user").toString(), MusicQPlayList.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void didFailWithMessage(final String message) {
                    Handler mainHandler = new Handler(EnterRoomActivity.this.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EnterRoomActivity.this, message, Toast.LENGTH_SHORT).show();
                            stopSpinner();
                        }
                    });
                }
            });
        }

    }

    private boolean validateCreateCall() {
        if(TextUtils.isEmpty(playlistName.getText().toString())) {
            Toast.makeText(EnterRoomActivity.this, "Please Enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void startSpinner() {
        loadingSpinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fast_rotator);
        loadingSpinnerImageView.startAnimation(animation);
    }

    private void stopSpinner() {
        loadingSpinner.setVisibility(View.GONE);
        loadingSpinnerImageView.clearAnimation();
    }

    private void goToMainActivity() {
        Intent myIntent = new Intent(EnterRoomActivity.this, MainActivity.class);
        startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        ButterKnife.inject(this);
    }
}
