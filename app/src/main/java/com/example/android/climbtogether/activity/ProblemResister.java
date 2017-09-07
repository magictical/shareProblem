package com.example.android.climbtogether.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.climbtogether.Model.Problem;
import com.example.android.climbtogether.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

import static com.example.android.climbtogether.activity.GymResister.RC_SELECT_PHOTO;

public class ProblemResister extends AppCompatActivity {
    private static final String LOG_TAG = ProblemResister.class.getName();

    //Problem's variables
    private String pName;
    private String pPhotoUriString;
    private String pLevel;
    private String pCreator;
    private String pFinisher;
    private String pChallenger;
    private String pExpireDay;

    //Image view of problem photo
    private ImageView mProblemImageView;

    //EditTextBox's variables
    private EditText mEditProblemName;
    private EditText mEditProblemLevel;
    private EditText mEditProblemCreator;
    private EditText mEditProblemFinisher;
    private EditText mEditProblemChallenger;
    private EditText mEditProblemExpireDay;

    //button for load image
    private Button mSelectImage;

    //button for add problem
    private Button mAddProblem;

    //add FireBase Database instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProblemDatabaseReference;

    //add Firebase Storage
    private FirebaseStorage mFireBaseStorage;
    private StorageReference mProblemStorageReference;

    private Uri mProblemPhotoUri;

    //storage에 업로드할 ref
    private StorageReference presentProblemPhotoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_resister);

        //Access point of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProblemDatabaseReference = mFirebaseDatabase.getReference().child("problem_data");

        //Access point of storage
        mFireBaseStorage = FirebaseStorage.getInstance();
        ////a reference to get the gym photos data in Firebase Storage.
        mProblemStorageReference = mFireBaseStorage.getReference().child("problem_photos");

        //ref ImageView and select Image Button
        mProblemImageView = (ImageView) findViewById(R.id.resister_problem_photo);
        mSelectImage = (Button) findViewById(R.id.resister_problem_load_image_button);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoSelectorIntent = new Intent(Intent.ACTION_PICK);
                photoSelectorIntent.setType("image/*");
                startActivityForResult(photoSelectorIntent, RC_SELECT_PHOTO);
            }
        });

        //bind EditText boxes to variables
        mEditProblemName = (EditText) findViewById(R.id.resister_problem_name);
        mEditProblemLevel = (EditText) findViewById(R.id.resister_problem_level);
        mEditProblemCreator = (EditText) findViewById(R.id.resister_problem_creator);
        mEditProblemFinisher = (EditText) findViewById(R.id.resister_problem_finisher);
        mEditProblemChallenger = (EditText) findViewById(R.id.resister_problem_challenger);
        mEditProblemExpireDay = (EditText) findViewById(R.id.resister_problem_expireday);

        mAddProblem = (Button) findViewById(R.id.resister_add_problem);
        mAddProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadPhotoToStorage();



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntentData) {
        super.onActivityResult(requestCode, resultCode, returnIntentData);

        switch (requestCode) {
            case RC_SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    mProblemPhotoUri = returnIntentData.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(mProblemPhotoUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    mProblemImageView.setImageURI(mProblemPhotoUri);
                }
        }
    }
    public void upLoadPhotoToStorage() {
        presentProblemPhotoRef =
                mProblemStorageReference.child(mProblemPhotoUri.getLastPathSegment());
        //Upload ifle to Firebase Storage
        presentProblemPhotoRef.putFile(mProblemPhotoUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        //save problem Photo Uri to instance


                        try {
                            pName =mEditProblemName.getText().toString();
                            pPhotoUriString = downloadUri.toString();
                            pLevel = mEditProblemLevel.getText().toString();
                            pCreator = mEditProblemCreator.getText().toString();
                            pFinisher = mEditProblemFinisher.getText().toString();
                            pChallenger = mEditProblemChallenger.getText().toString();
                            pExpireDay = mEditProblemExpireDay.getText().toString();
                        } catch (Exception e) {
                            //필수 입력사항이 빠졌을 경우 보여줄 에러메세지는 여기에 표기할것(조건식으로)
                            e.printStackTrace();
                        }
                        Problem problem = new Problem(pName, pPhotoUriString, pLevel, pCreator, pFinisher, pChallenger,
                                pExpireDay);
                        //add problem data to Firebase Database
                        mProblemDatabaseReference.push().setValue(problem);


                        Toast.makeText(ProblemResister.this, "Problem has been added",Toast.LENGTH_SHORT).show();
                        Log.v(LOG_TAG, "problem photo Uri's String value is " + pPhotoUriString);
                    }
                });
    }
}
