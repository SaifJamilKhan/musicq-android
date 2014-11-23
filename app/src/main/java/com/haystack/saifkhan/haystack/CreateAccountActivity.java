package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.haystack.saifkhan.haystack.Models.MusicQUser;

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


    @OnClick(R.id.send_create_account_button)
    public void createAccountClicked() {
        if (validate()) {
            MusicQUser user = new MusicQUser();
            user.name = userNameTxt.getText().toString();
            user.email = emailTxt.getText().toString();
            user.password = passwordTxt.getText().toString();
            user.passwordConfirmation = passwordConfirmTxt.getText().toString();

            try {
                NetworkUtils.createAccountCall(user, new NetworkUtils.CreateAccountListener() {
                    @Override
                    public void didCreateUser(final MusicQUser user) {

                        Handler mainHandler = new Handler(CreateAccountActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateAccountActivity.this, "Created user with id " + user.id, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void didFailWithMessage(final String message) {
                        Handler mainHandler = new Handler(CreateAccountActivity.this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validate() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.inject(this);
    }

}
