package com.example.android.climbtogether;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GymAdapter mGymAdapter;

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
    }


}
