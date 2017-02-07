package com.mhstudio.gasfinder.gaslistpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mhstudio.gasfinder.R;

public class GaslistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaslist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_subtitle_gaslist);
    }
}
