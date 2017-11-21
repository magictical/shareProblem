package com.example.android.climbtogether.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.climbtogether.Model.Problem;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.viewHolder.ProblemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.android.climbtogether.activity.GymDetailActivity.EXTRA_GYM_DETAIL_KEY;
//특정 짐에 속한 모든 problem  불러오기
public class ProblemListFromGym extends AppCompatActivity {
    public final String LOG_TAG = ProblemListFromGym.class.getName();

    private String gymPrimeKey;

    private DatabaseReference mSpecificGymRef;

    private ChildEventListener mChildEventListener;
    private RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter<Problem, ProblemViewHolder> mFirebaseRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_list);

        gymPrimeKey = getIntent().getStringExtra(EXTRA_GYM_DETAIL_KEY);
        if(gymPrimeKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_GYM_DETAIL_KEY");
        }

        mSpecificGymRef = FirebaseDatabase.getInstance().getReference().child("belong_problems")
                .child(gymPrimeKey);


        mRecyclerView = (RecyclerView) findViewById(R.id.problem_list);
        mRecyclerView.setHasFixedSize(true);

        Log.v(LOG_TAG, "check prime key and route : " + mSpecificGymRef);

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Problem, ProblemViewHolder>(
                Problem.class,
                R.layout.listitem_problem,
                ProblemViewHolder.class,
                mSpecificGymRef
        ) {
            @Override
            protected void populateViewHolder(ProblemViewHolder viewHolder, Problem model, int position) {
                final DatabaseReference refInGym = getRef(position);

                final String problemPrimeKey = refInGym.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProblemDetailActivity.class);
                        intent.putExtra(ProblemDetailActivity.EXTRA_PROBLEM_DETAIL_KEY, problemPrimeKey);
                        startActivity(intent);
                    }
                });
                viewHolder.bindToProblem(model);
            }
        };
        mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
