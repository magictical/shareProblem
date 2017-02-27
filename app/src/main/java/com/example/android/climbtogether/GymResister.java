package com.example.android.climbtogether;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.action;

/**
 * Created by MD on 2017-02-16.
 */

public class GymResister extends Activity {
    public static final String LOG_TAG = GymResister.class.getName();
    private String gymName;
    private String gymLocation;
    private String gymContact;
    private int gymPrice;
    private int gymPhotoResourceId;


    private EditText mEditGymName;
    private EditText mEditGymLocation;
    private EditText mEditGymContact;
    private EditText mEditGymPrice;
    private EditText mEditGymPhotoResoucrId;

    /*//add FireBase Database instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGymDatabaseReference;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_resister);

        /*//Access point of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //A reference to get a specific part of database
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");*/




        mEditGymName = (EditText) findViewById(R.id.resister_gym_name);
        mEditGymName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                //ACTION중 SEND가 실행때 아래코드가 실행됨
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    gymName = mEditGymName.getText().toString();
                }
                return handled;
            }
        });

        //각 user input을 인스턴스와 연결함
        mEditGymLocation = (EditText) findViewById(R.id.resister_gym_location);
        mEditGymContact = (EditText) findViewById(R.id.resister_gym_contact);
        mEditGymPrice = (EditText) findViewById(R.id.resister_gym_price);
        //사진은 다시 업로드 가능하도록 바꿔야함
        mEditGymPhotoResoucrId = (EditText) findViewById(R.id.resister_gym_photo);


        TextView saveGymInfo = (TextView) findViewById(R.id.check_gym_name);
        saveGymInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                gymName = mEditGymName.getText().toString();
                gymLocation = mEditGymLocation.getText().toString();
                gymContact = mEditGymContact.getText().toString();
                gymPhotoResourceId = Integer.valueOf(mEditGymPhotoResoucrId.getText().toString());
                gymPrice = Integer.valueOf(mEditGymPrice.getText().toString());


                /*Gym gym = new Gym(gymName, gymLocation, gymPhotoResourceId, gymContact, gymPrice);*/


                Log.v(LOG_TAG, "gym name is " + gymName + "\n"
                                + "gym Location is" + gymLocation + "\n"
                                + "gym Contact is" + gymContact + "\n"
                                + "gym Price is " + gymPrice + "\n"
                                + "gym Photo is not ready" + gymPhotoResourceId + "\n");

                Toast.makeText(GymResister.this, gymName + " " + gymLocation+ " " + gymContact
                        + gymPrice+ " "+ gymPhotoResourceId, Toast.LENGTH_SHORT).show();
            }
        });








    }
}
