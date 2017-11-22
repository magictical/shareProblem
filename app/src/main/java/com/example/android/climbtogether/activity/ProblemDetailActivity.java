package com.example.android.climbtogether.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.climbtogether.Model.Problem;
import com.example.android.climbtogether.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by MD on 2017-08-08.
 */

public class ProblemDetailActivity extends AppCompatActivity {

    public static final String TAG = ProblemDetailActivity.class.getName();

    public static final String EXTRA_PROBLEM_DETAIL_KEY = "problem_detail_key";

    private String mProblemDetailKey;

    private DatabaseReference mProblemReference;

    private ValueEventListener mProblemValueListener;

    //instances in the view
    private ImageView mDetailProblemImage;
    private TextView mDetailProblemName;
    private TextView mDetailProblemLevel;
    private TextView mDetailProblemDate;
    private TextView mDetailProblemSubscribers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_detail_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_for_activities);
        setSupportActionBar(toolbar);

        mProblemDetailKey = getIntent().getStringExtra(EXTRA_PROBLEM_DETAIL_KEY);
        if(mProblemDetailKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_PROBLEM_DETAIL_KEY");
        }

        mProblemReference = FirebaseDatabase.getInstance().getReference()
                .child("problem_data").child(mProblemDetailKey);

        //init views
        mDetailProblemImage = (ImageView) findViewById(R.id.detail_problem_image_view);
        mDetailProblemName = (TextView) findViewById(R.id.detail_problem_name);
        mDetailProblemLevel = (TextView) findViewById(R.id.detail_problem_level);
        mDetailProblemDate = (TextView) findViewById(R.id.detail_problem_birth_date);
        mDetailProblemSubscribers = (TextView) findViewById(R.id.detail_problem_subscribers);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Add Progressbar in the imageView
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.problem_image_progressbar);
        //Add value event listener to the Gym
        ValueEventListener problemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Problem problem = dataSnapshot.getValue(Problem.class);
                Glide.with(mDetailProblemImage.getContext())
                        .load(problem.getProblemPhotoUri())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mDetailProblemImage);

                mDetailProblemName.setText(problem.getProblemName());
                mDetailProblemLevel.setText(problem.getProblemLevel());
                mDetailProblemDate.setText(problem.getProblemExpireDay());
                mDetailProblemSubscribers.setText(problem.getProblemFinisher());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Getting Post failed, log a mes
                Log.w(TAG, "loadGym : onCancelled", databaseError.toException());
                //
                Toast.makeText(ProblemDetailActivity.this, "Failed to load gym Data",
                        Toast.LENGTH_SHORT).show();

            }
        };
        mProblemReference.addValueEventListener(problemListener);

        mProblemValueListener = problemListener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Remove the gym value event listener
        if(mProblemValueListener != null) {
            mProblemReference.removeEventListener(mProblemValueListener);
        }
    }
}
