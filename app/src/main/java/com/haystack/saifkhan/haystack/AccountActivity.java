package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AccountActivity extends Activity {

    @OnClick(R.id.login_btn)
    public void onLoginPressed(View view) {
        Intent myIntent = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    @OnClick(R.id.signup_button)
    public void onSignupPressed(View view) {
        Intent myIntent = new Intent(AccountActivity.this, CreateAccountActivity.class);
        startActivity(myIntent);
    }

    @InjectView(R.id.logo)
    ImageView logoImageView;

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

        SharedPreferences sharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
        if(sharedPreferences.contains("auth_token")) {
            String authToken = sharedPreferences.getString("auth_token", "");
            if(!TextUtils.isEmpty(authToken)) {
                goToEnterRoomActivity();
                return;
            }
        }
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotator);
        logoImageView.startAnimation(animation);
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
