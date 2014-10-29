package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {

    @OnClick(R.id.login_btn)
    public void onLoginPressed(View view) {
        goToEnterRoomActivity();
    }

    @OnClick(R.id.signup_button)
    public void onSignupPressed(View view) {
        goToEnterRoomActivity();
    }

    private void goToEnterRoomActivity() {
        Intent myIntent = new Intent(LoginActivity.this, EnterRoomActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
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
