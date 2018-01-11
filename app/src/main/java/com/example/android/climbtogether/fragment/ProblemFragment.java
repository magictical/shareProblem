package com.example.android.climbtogether.fragment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.climbtogether.Model.Gym;
import com.example.android.climbtogether.Model.Problem;
import com.example.android.climbtogether.ProblemAdapter;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.activity.GymDetailActivity;
import com.example.android.climbtogether.activity.ProblemDetailActivity;
import com.example.android.climbtogether.activity.ProblemResister;
import com.example.android.climbtogether.viewHolder.ProblemViewHolder;
import com.example.android.climbtogether.viewHolder.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    //ref for test order by gym name
    DatabaseReference mGymDatabaseReference;

    //child DB listener
    ChildEventListener mChildEventListener;

    //change array Adapter to FirebaseRecyclerAdapter by using RecyclerView

    //Add RecyclerView
    RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter<Gym, ViewHolder> mFirebaseRecyclerAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    public ProblemFragment() {}






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.problem_list, container, false);

        //add Access point of FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        /*mProblemDatabaseReference = mFirebaseDatabase.getReference().child("problem_data");*/

        //Gym ref
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");





                //putExtra를 받아서 ref만 요청에따라 바뀌는 식으로 recylerAdapter를 구성하면 될듯\

        mAddProblem = (Button) rootView.findViewById(R.id.resister_problem_button);
        mAddProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent problemResisterIntent = new Intent(getActivity(), ProblemResister.class);
                getActivity().startActivity(problemResisterIntent);
            }
        });

        /*ArrayList<Problem> problems = new ArrayList<Problem>();
        mProblemAdapter = new ProblemAdapter(getActivity(), problems);
        ListView listview = (ListView) rootView.findViewById(R.id.problem_list);
        listview.setAdapter(mProblemAdapter);*/

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.problem_list);
        mRecyclerView.setHasFixedSize(true);

        return rootView;


    }

    private void attachDatabaseListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Problem problem = dataSnapshot.getValue(Problem.class);
                    /*
                    mProblemAdapter.add(problem);
                    */
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
            /*mProblemDatabaseReference.addChildEventListener(mChildEventListener);*/
            mGymDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseListener() {
        if (mChildEventListener != null) {
            /*mProblemDatabaseReference.removeEventListener(mChildEventListener);*/
            mGymDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        /*boolean iconClicked = true;
        if(iconClicked)*/

        //set order
        mLinearLayoutManager.setReverseLayout(false);

        //if true start the list view from the bottom
        mLinearLayoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //order by name
        Query nameQuery = mGymDatabaseReference.orderByChild("gymName");


        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Gym, ViewHolder>(
                Gym.class,
                R.layout.listitem_in_gym,
                ViewHolder.class,
                nameQuery
        ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Gym model, int position) {
                final DatabaseReference problemRef = getRef(position);
                //Set Click listener for the Problem detail view
                final String gymDetailKey = problemRef.getKey();



                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //launch problem DetailView
                        Intent intent = new Intent(getActivity(), GymDetailActivity.class);
                        intent.putExtra(GymDetailActivity.EXTRA_GYM_DETAIL_KEY, gymDetailKey);
                        startActivity(intent);
                    }
                });
                viewHolder.bindToGym(model);
            }
        };
        mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
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
