package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.haystack.saifkhan.haystack.Models.MusicQUser;
import com.haystack.saifkhan.haystack.Utils.NetworkUtils;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by saifkhan on 14-11-23.
 */
public class CreateAccountActivity extends Activity {

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
        if (validate()) {
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

                        Handler mainHandler = new Handler(CreateAccountActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateAccountActivity.this, "Created user with id " + user.id, Toast.LENGTH_LONG).show();
                                stopSpinner();
                            }
                        });
                        SharedPreferences sharedPreferences = getSharedPreferences(CreateAccountActivity.this.getPackageName(), MODE_PRIVATE);
                        sharedPreferences.edit().putString("auth_token", user.authToken).apply();
                        sharedPreferences.edit().putString("name", user.name).apply();
                        startMainActivity();
                    }

                    @Override
                    public void didFailWithMessage(final String message) {
                        Handler mainHandler = new Handler(CreateAccountActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_SHORT).show();
                                stopSpinner();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, EnterRoomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
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
