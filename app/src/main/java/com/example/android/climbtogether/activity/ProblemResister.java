package com.example.android.climbtogether.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.climbtogether.Problem;
import com.example.android.climbtogether.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProblemResister extends AppCompatActivity {
    //Problem's variables
    String pName;
    int pPhotoResourceId;
    String pLevel;
    String pCreator;
    String pFinisher;
    String pChallenger;
    String pExpireDay;

    //EditTextBox's variables
    private EditText mEditProblemName;
    private EditText mEditProblemPhotoResourceId;
    private EditText mEditProblemLevel;
    private EditText mEditProblemCreator;
    private EditText mEditProblemFinisher;
    private EditText mEditProblemChallenger;
    private EditText mEditProblemExpireDay;

    //button for add problem
    private Button mAddProblem;

    //add FireBase Database instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProblemDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_resister);

        //Access point of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProblemDatabaseReference = mFirebaseDatabase.getReference().child("problem_data");

        //bind EditText boxes to variables
        mEditProblemName = (EditText) findViewById(R.id.resister_problem_name);
        mEditProblemPhotoResourceId = (EditText) findViewById(R.id.resister_problem_photo_resourceid);
        mEditProblemLevel = (EditText) findViewById(R.id.resister_problem_level);
        mEditProblemCreator = (EditText) findViewById(R.id.resister_problem_creator);
        mEditProblemFinisher = (EditText) findViewById(R.id.resister_problem_finisher);
        mEditProblemChallenger = (EditText) findViewById(R.id.resister_problem_challenger);
        mEditProblemExpireDay = (EditText) findViewById(R.id.resister_problem_expireday);

        mAddProblem = (Button) findViewById(R.id.resister_add_problem);
        mAddProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    pName =mEditProblemName.getText().toString();
                    pPhotoResourceId = Integer.valueOf(mEditProblemPhotoResourceId.getText().toString());
                    pLevel = mEditProblemLevel.getText().toString();
                    pCreator = mEditProblemCreator.getText().toString();
                    pFinisher = mEditProblemFinisher.getText().toString();
                    pChallenger = mEditProblemChallenger.getText().toString();
                    pExpireDay = mEditProblemExpireDay.getText().toString();
                } catch (Exception e) {
                    //필수 입력사항이 빠졌을 경우 보여줄 에러메세지는 여기에 표기할것(조건식으로)
                    e.printStackTrace();
                }
                Problem problem = new Problem(pName, pPhotoResourceId, pLevel, pCreator, pFinisher, pChallenger,
                        pExpireDay);
                //add problem data to Firebase Database
                mProblemDatabaseReference.push().setValue(problem);


                Toast.makeText(ProblemResister.this, "Problem has been added",Toast.LENGTH_SHORT).show();

            }
        });
















    }
}
