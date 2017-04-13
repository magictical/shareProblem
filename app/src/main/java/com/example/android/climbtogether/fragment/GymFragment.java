package com.example.android.climbtogether.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.climbtogether.Gym;
import com.example.android.climbtogether.GymAdapter;
import com.example.android.climbtogether.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by MD on 2017-03-09.
 */

public class GymFragment extends Fragment {

    GymAdapter mGymAdapter;

    //Add Gym button
    Button mGymResister;
    //Add Problem button
    Button mProblemResister;

    //add FireBaseDatabase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mGymDatabaseReference;

    //child Listener
    private ChildEventListener mChildEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gym_list, container, false);

        //Access point of DB
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //reference point of DB
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");

        attachDatabaseListener();


        //add new Gym ArrayList
        ArrayList<Gym> gym = new ArrayList<Gym>();
        mGymAdapter = new GymAdapter(getActivity(), gym);

        //Bind ListView
        ListView list = (ListView) rootView.findViewById(R.id.gym_list);
        list.setAdapter(mGymAdapter);

        mGymResister = (Button) rootView.findViewById(R.id.resister_gym_button);
        mGymResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent problemIntent = new Intent(getActivity(), ProblemActivity.class);
                startActivity(problemIntent);
            }
        });
        mProblemResister = (Button) rootView.findViewById(R.id.move_to_problem_list);
        mProblemResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProblemActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    //리스트가 갱신될때 DB에서 새 data를 업데이트 하기위한 리스너
    //!! 중요 라이프사이클에 Listener를 꺼주는 옵션도 줘야 자원절약
    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
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

    private void detachDatabaseListener() {
        if (mChildEventListener != null) {
            mGymDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseListener();
    }
}