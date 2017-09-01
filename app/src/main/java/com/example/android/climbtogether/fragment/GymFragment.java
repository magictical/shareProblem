package com.example.android.climbtogether.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.climbtogether.Model.Gym;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.activity.GymDetailActivity;
import com.example.android.climbtogether.activity.GymResister;
import com.example.android.climbtogether.viewHolder.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by MD on 2017-03-09.
 */

public class GymFragment extends Fragment {
    public static final String TAG = GymFragment.class.getName();

//    GymAdapter mGymAdapter;

    //Add Gym button
    Button mGymResister;
    //Add Problem button
    Button mProblemResister;

    //RecyclerView
    RecyclerView mRecyclerView;

    //add FireBaseDatabase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mGymDatabaseReference;

    private FirebaseRecyclerAdapter<Gym, ViewHolder>  mFirebaseRecyclerAdapter;

    private  LinearLayoutManager mLinearLayoutManager;

    FirebaseStorage mFirebaseStorage;
    StorageReference mGymStorageReference;

    //child Listener
    private ChildEventListener mChildEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gym_list, container, false);

        //Access point of DB
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //reference point of DB
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");

        /*//add new Gym ArrayList
        ArrayList<Gym> gym = new ArrayList<Gym>();
        mGymAdapter = new GymAdapter(getActivity(), gym);

        //Bind ListView
        ListView list = (ListView) rootView.findViewById(R.id.gym_list);
        list.setAdapter(mGymAdapter);*/


        //Move to Gym resister activity when it clicked
        mGymResister = (Button) rootView.findViewById(R.id.resister_gym_button);
        mGymResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent problemIntent = new Intent(getActivity(), GymResister.class);
                startActivity(problemIntent);
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.gym_list);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Gym, ViewHolder>(
                Gym.class,
                R.layout.listitem_in_gym,
                ViewHolder.class,
                mGymDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Gym model, int position) {
                final DatabaseReference gymRef = getRef(position);
                //Set click listener for the Gym detail view
                final String gymDetailKey = gymRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //launch gymDetailView
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

    //리스트가 갱신될때 DB에서 새 data를 업데이트 하기위한 리스너
    //!! 중요 라이프사이클에 Listener를 꺼주는 옵션도 줘야 자원절약
    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Gym gym = dataSnapshot.getValue(Gym.class);
                    /*mFirebaseRecyclerAdapter.(gym);*/
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