package com.ws.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.ws.notes.ui.IntroActivity;
import com.ws.notes.utils.PreferenceManager;

public class StartActivity extends AppCompatActivity {

    private PreferenceManager preferences;
    private static int REQUEST_CODE_INTRO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        preferences = new PreferenceManager(this.getApplicationContext());
        if (preferences.isFirstLaunch()) {
            startActivityForResult(new Intent(this, IntroActivity.class), REQUEST_CODE_INTRO);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                finish();
                }
                }, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_INTRO) {
            preferences.setFirstLaunch(false);
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
