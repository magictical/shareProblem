package com.example.android.climbtogether.activity;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;


import com.example.android.climbtogether.R;

public class UserProfileEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_for_activities);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.profile_edit_user_img_view);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_outline));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }
}
