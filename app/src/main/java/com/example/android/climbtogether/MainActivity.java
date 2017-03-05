package com.example.android.climbtogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GymAdapter mGymAdapter;
    Button mGymResister;
    Button mMoveToProblemList;

    //add FireBaseDatabase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mGymDatabaseReference;

    //child Listener
    ChildEventListener mChildEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_list);

        //Access point of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //A reference to get a specific part of database
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");



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

        mMoveToProblemList = (Button) findViewById(R.id.move_to_problem_list);
        mMoveToProblemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent problemIntent = new Intent(MainActivity.this, ProblemActivity.class);
                startActivity(problemIntent);
            }
        } );


        //리스트를 업데이트하면 DB에서 Gym data를 가져오는 리스너
        attachDatabaseListener();
    }
    //리스트가 갱신될때 DB에서 새 data를 업데이트 하기위한 리스너
    //!! 중요 라이프사이클에 Listener를 꺼주는 옵션도 줘야 자원절약
    private void attachDatabaseListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Gym gym = dataSnapshot.getValue(Gym.class);
                    mGymAdapter.add(gym);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mGymDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }






}
