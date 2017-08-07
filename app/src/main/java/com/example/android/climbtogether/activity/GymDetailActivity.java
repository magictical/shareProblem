package com.example.android.climbtogether.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.climbtogether.Gym;
import com.example.android.climbtogether.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GymDetailActivity extends AppCompatActivity {

    public static final String TAG = GymDetailActivity.class.getName();

    public static final String EXTRA_GYM_DETAIL_KEY = "gym_detail_key";

    private DatabaseReference mGymReference;

    private ValueEventListener mGymListener;

    private String mGymDetailKey;

    //instances in the detailView
    private ImageView mDetailGymImage;
    private TextView mDetailGymName;
    private TextView mDetailContactNumb;
    private TextView mDetailLocation;
    private TextView mDetailPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_detail_view);

        mGymDetailKey = getIntent().getStringExtra(EXTRA_GYM_DETAIL_KEY);
        if(mGymDetailKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_GYM_DETAIL_KEY");
        }

        mGymReference = FirebaseDatabase.getInstance().getReference()
                .child("gym_data").child(mGymDetailKey);

        //init Views
        mDetailGymImage = (ImageView) findViewById(R.id.detail_gym_image_view);
        mDetailGymName = (TextView) findViewById(R.id.detail_gym_name);
        mDetailContactNumb = (TextView) findViewById(R.id.detail_gym_contact_number);
        mDetailLocation = (TextView) findViewById(R.id.detail_gym_location);
        mDetailPayment = (TextView) findViewById(R.id.detail_gym_payment);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Addd value event listener to the Gym
        // [START post_value_event_listener
        ValueEventListener gymListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gym gym = dataSnapshot.getValue(Gym.class);
                Glide.with(mDetailGymImage.getContext())
                        .load(gym.getGymPhotoUri())
                        .into(mDetailGymImage);
                mDetailGymName.setText(gym.getGymName());
                mDetailContactNumb.setText(gym.getGymContact());
                mDetailLocation.setText(gym.getGymLocation());
                mDetailPayment.setText(Integer.toString(gym.getGymPrice()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Getting Post failed, log a mes
                Log.w(TAG, "loadGym : onCancelled", databaseError.toException());
                //
                Toast.makeText(GymDetailActivity.this, "Failed to load gym Data",
                        Toast.LENGTH_SHORT).show();

            }
        };
        mGymReference.addValueEventListener(gymListener);
        //[END gym_value_event_listener

        //Keep copy of event listener so we can remove it when app stops
        mGymListener = gymListener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Remove the gym value event listener
        if(mGymListener != null) {
            mGymReference.removeEventListener(mGymListener);
        }
    }
}
