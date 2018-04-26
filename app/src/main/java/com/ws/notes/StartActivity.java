package com.ws.notes;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterViewFlipper;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final AdapterViewFlipper mAdapterViewFlipper=findViewById(R.id.mAdapterViewFlipper);
        int[] mData=new int[]{R.drawable.start1};
        mAdapterViewFlipper.setAdapter(new ViewFilipperAdapter(this,mData));
        mAdapterViewFlipper.startFlipping();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                finish();
            }
        }, 5000);
    }
}
