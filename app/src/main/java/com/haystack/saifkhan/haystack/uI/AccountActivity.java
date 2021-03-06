package com.haystack.saifkhan.haystack.uI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import com.haystack.saifkhan.haystack.Models.MusicQLoginCall;
import com.haystack.saifkhan.haystack.Models.MusicQUser;
import com.haystack.saifkhan.haystack.R;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AccountActivity extends Activity {

    @InjectView(R.id.login_email_text)
    public TextView emailTextView;

    @InjectView(R.id.login_password_text)
    public TextView passwordTextView;


    @InjectView(R.id.background_video_view)
    VideoView backgroundVideoView;


    @InjectView(R.id.view_flipper)
    ViewFlipper viewFlipper;

    @OnClick(R.id.login_switch_btn)
    public void loginSwitchButton() {
        viewFlipper.setDisplayedChild(0);
    }

    @OnClick(R.id.register_switch_btn)
    public void registerSwitchButton() {
        viewFlipper.setDisplayedChild(1);
    }

    @OnClick(R.id.login_btn)
    public void onLoginPressed(View view) {
        MusicQLoginCall user = new MusicQLoginCall();
        user.email = emailTextView.getText().toString();
        user.password = passwordTextView.getText().toString();
        startSpinner();
        try {
            NetworkUtils.loginCall(user, new NetworkUtils.NetworkCallListener() {
                @Override
                public void didSucceed() {
                    Handler mainHandler = new Handler(AccountActivity.this.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopSpinner();
                            goToEnterRoomActivity();
                        }
                    });
                }

                @Override
                public void didSucceedWithJson(JSONObject body) {

                }

                @Override
                public void didFailWithMessage(final String message) {
                    Handler mainHandler = new Handler(AccountActivity.this.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                            stopSpinner();
                        }
                    });
                }
            }, this);
        } catch (Exception e) {
            e.printStackTrace();
            stopSpinner();
        }
    }

    @InjectView(R.id.create_account_user_name_text)
    TextView userNameTxt;

    @InjectView(R.id.create_account_email_text)
    TextView emailTxt;

    @InjectView(R.id.create_account_password_confirm_text)
    TextView passwordConfirmTxt;

    @InjectView(R.id.create_account_password_text)
    TextView passwordTxt;

    @InjectView(R.id.loading_spinner)
    View loadingSpinner;

    @InjectView(R.id.loading_spinner_image_view)
    View loadingSpinnerImageView;

    @OnClick(R.id.send_create_account_button)
    public void createAccountClicked() {
            MusicQUser user = new MusicQUser();
            user.name = userNameTxt.getText().toString();
            user.email = emailTxt.getText().toString();
            user.password = passwordTxt.getText().toString();
            user.passwordConfirmation = passwordConfirmTxt.getText().toString();
            startSpinner();

            try {
                NetworkUtils.createAccountCall(user, new NetworkUtils.CreateAccountListener() {
                    @Override
                    public void didCreateUser(final MusicQUser user) {

                        Handler mainHandler = new Handler(AccountActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AccountActivity.this, "Created user with id " + user.id, Toast.LENGTH_LONG).show();
                                stopSpinner();
                            }
                        });
                        SharedPreferences sharedPreferences = getSharedPreferences(AccountActivity.this.getPackageName(), MODE_PRIVATE);
                        sharedPreferences.edit().putString("auth_token", user.authToken).apply();
                        sharedPreferences.edit().putString("name", user.name).apply();
                        sharedPreferences.edit().putString("email", user.email).apply();
                        goToEnterRoomActivity();
                    }

                    @Override
                    public void didFailWithMessage(final String message) {
                        Handler mainHandler = new Handler(AccountActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                                stopSpinner();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
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


//    @OnClick(R.id.signup_button)
//    public void onSignupPressed(View view) {
//        Intent myIntent = new Intent(AccountActivity.this, CreateAccountActivity.class);
//        startActivity(myIntent);
//    }

    private void goToEnterRoomActivity() {
        Intent myIntent = new Intent(AccountActivity.this, EnterRoomActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.inject(this);
        backgroundVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.fade_out));

        SharedPreferences sharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
        if(sharedPreferences.contains("auth_token")) {
            String authToken = sharedPreferences.getString("auth_token", "");
            if(!TextUtils.isEmpty(authToken)) {
                goToEnterRoomActivity();
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.carlsagan);

        backgroundVideoView.setVideoURI(uri);
        backgroundVideoView.start();
        ViewGroup.LayoutParams layoutParams = backgroundVideoView.getLayoutParams();
        layoutParams.width = 1280;
        layoutParams.height = 720;
        backgroundVideoView.setLayoutParams(layoutParams);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.login, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
