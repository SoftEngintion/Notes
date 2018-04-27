package com.ws.notes;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterViewFlipper;

import com.ws.notes.ui.IntroActivity;
import com.ws.notes.utils.PreferenceManager;

import static com.ws.notes.MainActivity.isDebug;

public class StartActivity extends AppCompatActivity {

    private PreferenceManager preferences;
    int REQUEST_CODE_INTRO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final AdapterViewFlipper mAdapterViewFlipper=findViewById(R.id.mAdapterViewFlipper);
        int[] mData=new int[]{R.drawable.start1,R.drawable.start2,R.drawable.start3};
        mAdapterViewFlipper.setAdapter(new ViewFilipperAdapter(this,mData));
        mAdapterViewFlipper.startFlipping();
        preferences = new PreferenceManager(this.getApplicationContext());
        if (preferences.isFirstLaunch()) {
            startActivityForResult(new Intent(this, IntroActivity.class), REQUEST_CODE_INTRO);
//            Log.d(TAG, "initComponent: IntroActivity");
//            startActivity(new Intent(MainActivity.this, IntroActivity.class));
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                finish();
                }
                }, 3000);
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
