package com.haystack.saifkhan.haystack;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by saifkhan on 2014-10-28.
 */
public class EnterRoomActivity extends Activity{

    @OnClick(R.id.existing_room_btn)
    public void onExistingPressed(View view) {
        finish();
    }

    @OnClick(R.id.new_room_btn)
    public void onNewRoomPressed(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        ButterKnife.inject(this);
    }
}
