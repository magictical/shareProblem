package com.example.android.climbtogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GymAdapter mGymAdapter;
    Button mGymResister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_list);


        final ArrayList<Gym> gym = new ArrayList<Gym>();
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym_photo, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym2, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym3, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym4, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym5, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym6, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym7, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym8, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym9, "010-1010-1010", 10000));
        gym.add(new Gym("RockOdyssey", "busan", R.drawable.gym10, "010-1010-1010", 10000));

        mGymAdapter = new GymAdapter(this, gym);

        ListView listView = (ListView) findViewById(R.id.gym_list);

        listView.setAdapter(mGymAdapter);

        mGymResister = (Button) findViewById(R.id.resister_gym_button);
        mGymResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gymResisterIntent = new Intent(MainActivity.this, GymResister.class);
                startActivity(gymResisterIntent);

            }
        });


    }

    //gym 추가 리스너 클릭하면 gym추가 Activity실행(intent 시작)





}
