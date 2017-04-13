package com.example.android.climbtogether.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.climbtogether.Problem;
import com.example.android.climbtogether.ProblemAdapter;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.activity.ProblemResister;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProblemActivity extends AppCompatActivity {
    ProblemAdapter mProblemAdapter;
    Button mAddProblem;

    //add FireBaseDatabase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mProblemDatabaseReference;

    //child DB listener
    ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_list);

        //add Access point of Firebase Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProblemDatabaseReference = mFirebaseDatabase.getReference().child("problem_data");



        mAddProblem = (Button) findViewById(R.id.resister_problem_button);
        mAddProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent problemResisterIntent = new Intent(ProblemActivity.this, ProblemResister.class);
                startActivity(problemResisterIntent);
            }
        });

    final ArrayList<Problem> problems = new ArrayList<Problem>();

        mProblemAdapter = new ProblemAdapter(this, problems);
        ListView listview = (ListView) findViewById(R.id.problem_list);
        listview.setAdapter(mProblemAdapter);

        attachDatabaseListener();
    }


    private void attachDatabaseListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Problem problem = dataSnapshot.getValue(Problem.class);
                    mProblemAdapter.add(problem);
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
            mProblemDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }


}
