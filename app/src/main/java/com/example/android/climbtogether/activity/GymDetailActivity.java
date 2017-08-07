package com.example.android.climbtogether.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.climbtogether.R;

public class GymDetailActivity extends AppCompatActivity {

    public static final String TAG = GymDetailActivity.class.getName();

    public static final String EXTRA_GYM_DETAIL_KEY = "gym_detail_key";


    private String mGymDetailKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_detail_view);

    }



}
