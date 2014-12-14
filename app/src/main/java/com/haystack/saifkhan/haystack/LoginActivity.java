package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.haystack.saifkhan.haystack.Models.MusicQLoginCall;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by saifkhan on 14-12-14.
 */
public class LoginActivity extends Activity {

    @InjectView(R.id.loading_spinner)
    View loadingSpinner;

    @InjectView(R.id.loading_spinner_image_view)
    View loadingSpinnerImageView;

    @InjectView(R.id.login_email_text)
    TextView emailTextView;

    @InjectView(R.id.login_password_text)
    TextView passwordTextView;

    @OnClick(R.id.login_with_cred_btn)
    public void onLoginPressed(View view) {
        if (validate()) {
            MusicQLoginCall user = new MusicQLoginCall();
            user.email = emailTextView.getText().toString();
            user.password = passwordTextView.getText().toString();
            startSpinner();
            try {
                NetworkUtils.loginCall(user, new NetworkUtils.NetworkCallListener() {
                    @Override
                    public void didSucceed() {
                        Handler mainHandler = new Handler(LoginActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                stopSpinner();
                                launchMainActivity();
                            }
                        });
                    }

                    @Override
                    public void didFailWithMessage(final String message) {
                        Handler mainHandler = new Handler(LoginActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, EnterRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

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


    private boolean validate() {
        return true;
    }
}
