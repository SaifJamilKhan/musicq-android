package com.haystack.saifkhan.haystack.uI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.DatabaseManager;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class EnterRoomActivity extends Activity{

    @InjectView(R.id.playlist_name)
    EditText playlistName;

    @InjectView(R.id.playlist_description)
    EditText playlistDescription;

    @InjectView(R.id.loading_spinner)
    public View loadingSpinner;

    @InjectView(R.id.recents_list)
    public LinearLayout recentsList;

    @InjectView(R.id.loading_spinner_image_view)
    public View loadingSpinnerImageView;

    @InjectView(R.id.existing_playlist_pin)
    TextView existingPlaylistPin;

    @InjectView(R.id.add_circle_button)
    CustomFAB circleFABButton;

    private EnterQueueViewHolder mEnterQueueViewHolder;

    @InjectView(R.id.enter_queue_view)
    public View mEnterQueueView;

//    private PopupWindow popWindow;

    @OnClick(R.id.add_circle_button)
    public void addPressed(View view) {

//        if(mEnterQueueView == null || mEnterQueueViewHolder == null) {
////            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            mEnterQueueView = layoutInflater.inflate(R.layout.enter_queue_popover, null, false);
//        }


        if(mEnterQueueView.getVisibility() != View.VISIBLE) {
            mEnterQueueView.setVisibility(View.VISIBLE);
            circleFABButton.rotateForward();
        } else {
            mEnterQueueView.setVisibility(View.GONE);
            circleFABButton.rotateBackward();
        }



//        if(popWindow == null || !popWindow.isShowing()) {
//            Display display = getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//
//            popWindow = new PopupWindow(mEnterQueueView, size.x - 50, size.y - 500, true);
//            popWindow.setFocusable(true);
//
//            popWindow.setOutsideTouchable(true);
//
//            popWindow.setAnimationStyle(R.anim.fade_in); // call this before showing the popup
//            popWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 150);  // 0 - X postion and 150 - Y position
//            circleFABButton.rotateForward();
//        } else {
//            circleFABButton.rotateBackward();
//            popWindow.dismiss();
//        }
    }

    public static class EnterQueueViewHolder {

        private final Activity activity;
        @InjectView(R.id.new_queue_btn)
        Button newQueueButton;

        @InjectView(R.id.join_queue_btn)
        Button joinQueueButton;

        @InjectView(R.id.new_queue_text_view)
        EditText queueIDNameTextView;

        @InjectView(R.id.join_queue_text_view)
        EditText joinQueueTextView;

        @InjectView(R.id.new_used_view_flipper)
        ViewFlipper viewFlipper;

        @OnClick(R.id.new_queue_btn)
        public void newQueueButtonPressed() {
            this.newQueueButton.setTextColor(activity.getResources().getColor(R.color.musicq_deep_red));
            this.joinQueueButton.setTextColor(activity.getResources().getColor(R.color.text_light_gray));
            viewFlipper.setDisplayedChild(0);
        }

        @OnClick(R.id.join_queue_btn)
        public void joinQueueButtonPressed() {
            this.newQueueButton.setTextColor(activity.getResources().getColor(R.color.text_light_gray));
            this.joinQueueButton.setTextColor(activity.getResources().getColor(R.color.musicq_deep_red));
            viewFlipper.setDisplayedChild(1);
        }

        public EnterQueueViewHolder(View view, Activity referenceContext){
            ButterKnife.inject(this, view);
            this.activity = referenceContext;
            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.fade_in));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.fade_out));
        }
    }
    @OnClick(R.id.existing_room_btn)
    public void onExistingPressed(View view) {
        final MusicQPlayList playlist = (MusicQPlayList) DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class).get(existingPlaylistPin.getText().toString());
        if(playlist != null && !TextUtils.isEmpty(playlist.id)) {
            SharedPreferences sharedPreferences = getSharedPreferences(EnterRoomActivity.this.getPackageName(), MODE_PRIVATE);
            sharedPreferences.edit().putString("currentPlaylistID", playlist.id).apply();
            goToMainActivity();
        } else {
            Toast.makeText(EnterRoomActivity.this, "Unable to find playlist " + existingPlaylistPin.getText().toString(), Toast.LENGTH_SHORT).show();
        }
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
                        final MusicQPlayList playlist = gson.fromJson(body.getJSONObject("playlist").toString(), MusicQPlayList.class);
                        if(!TextUtils.isEmpty(playlist.id)) {
                            DatabaseManager.getDatabaseManager().addObject(playlist);
                            SharedPreferences sharedPreferences = getSharedPreferences(EnterRoomActivity.this.getPackageName(), MODE_PRIVATE);
                            sharedPreferences.edit().putString("currentPlaylistID", playlist.id).apply();
                            Handler mainHandler = new Handler(EnterRoomActivity.this.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    stopSpinner();
                                    Toast.makeText(EnterRoomActivity.this, "Creates playlist with id " + playlist.id, Toast.LENGTH_SHORT).show();
                                    DatabaseManager.getDatabaseManager().addObject(playlist);
                                    goToMainActivity();
                                }
                            });
                        } else {
                            Handler mainHandler = new Handler(EnterRoomActivity.this.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EnterRoomActivity.this, "Failed to create", Toast.LENGTH_SHORT).show();
                                    stopSpinner();
                                }
                            });
                        }
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
            }, EnterRoomActivity.this);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        ButterKnife.inject(this);
        mEnterQueueViewHolder = new EnterQueueViewHolder(mEnterQueueView, this);
        mEnterQueueView.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        NetworkUtils.getAllPlaylists(new NetworkUtils.NetworkCallListener() {
            @Override
            public void didSucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylistsFromDatabase();
                    }
                });
            }

            @Override
            public void didSucceedWithJson(JSONObject body) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylistsFromDatabase();
                    }
                });
            }

            @Override
            public void didFailWithMessage(String message) {
                Timber.e(message);

            }
        }, this);
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPlaylistsFromDatabase();
    }

    @Override
    public void onBackPressed() {
        if(mEnterQueueView!= null && mEnterQueueView.getVisibility() == View.VISIBLE) {
            mEnterQueueView.setVisibility(View.GONE);
            circleFABButton.rotateBackward();
        } else {
            super.onBackPressed();
        }
    }

    private void loadPlaylistsFromDatabase() {
        HashMap<String, MusicQPlayList> playlists = DatabaseManager.getDatabaseManager().getHashmapForClass(MusicQPlayList.class);
        if ((playlists != null && playlists.size() > 0) && recentsList.getChildCount() != playlists.size()) {
            recentsList.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            for (MusicQPlayList playList : playlists.values()) {
                View view = inflater.inflate(R.layout.playlist_cell, null);
                recentsList.addView(view);
                PlaylistCellViewHolder holder = new PlaylistCellViewHolder(view);
                holder.playlistName.setText(playList.name);
                holder.playlistDescription.setText(playList.id.concat(" ") + playList.description);
            }
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

    public static class PlaylistCellViewHolder {

        @InjectView(R.id.name_text_view)
        TextView playlistName;

        @InjectView(R.id.description_text_view)
        TextView playlistDescription;

        public PlaylistCellViewHolder (View view){
            ButterKnife.inject(this, view);
        }
    }


}
