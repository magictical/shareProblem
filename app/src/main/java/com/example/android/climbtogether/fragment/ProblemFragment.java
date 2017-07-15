package com.example.android.climbtogether.fragment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.climbtogether.Problem;
import com.example.android.climbtogether.ProblemAdapter;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.activity.ProblemResister;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProblemFragment extends Fragment {

    public final String LOG_TAG = ProblemFragment.class.getName();
    ProblemAdapter mProblemAdapter;
    Button mAddProblem;

    //add FireBaseDatabase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mProblemDatabaseReference;

    //add FirebaseStorage
    FirebaseStorage mFirebaseStorage;
    StorageReference mProblemStorageReference;

    //child DB listener
    ChildEventListener mChildEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.problem_list, container, false);

        //add Access point of FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProblemDatabaseReference = mFirebaseDatabase.getReference().child("problem_data");

        //add Access point of FirebaseStorage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mProblemStorageReference = mFirebaseStorage.getReference().child("problem_photos");


        mAddProblem = (Button) rootView.findViewById(R.id.resister_problem_button);
        mAddProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent problemResisterIntent = new Intent(getActivity(), ProblemResister.class);
                getActivity().startActivity(problemResisterIntent);
            }
        });

        ArrayList<Problem> problems = new ArrayList<Problem>();
        mProblemAdapter = new ProblemAdapter(getActivity(), problems);
        ListView listview = (ListView) rootView.findViewById(R.id.problem_list);
        listview.setAdapter(mProblemAdapter);

        return rootView;


    }

//    public void passAuth(String sendMsg, FirebaseAuth auth, FirebaseAuth.AuthStateListener listener) {
//        check = sendMsg;
//        mFirebaseAuth = auth;
//        mAuthStateListener = listener;
//    }


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

    private void detachDatabaseListener() {
        if (mChildEventListener != null) {
            mProblemDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        attachDatabaseListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseListener();
    }
}
