package com.example.android.climbtogether.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import java.io.IOException;
import java.io.InputStream;

import static com.example.android.climbtogether.activity.GymDetailActivity.EXTRA_GYM_DETAIL_KEY;
import static com.example.android.climbtogether.activity.GymResister.RC_SELECT_PHOTO;

public class ProblemResister extends AppCompatActivity {
    private static final String LOG_TAG = ProblemResister.class.getName();

    //variable to Extra

    private String mGymPrimeKey;

    //request code for select problem photo
    public static final int RC_SELECT_PB_PHOTO = 101;


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
    //ref for belonged problem
    private DatabaseReference mBelongedDatabaseReference;

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

        //Addd Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_for_activities);
        setSupportActionBar(toolbar);

        mGymPrimeKey = getIntent().getStringExtra(EXTRA_GYM_DETAIL_KEY);

        if(mGymPrimeKey == null) {
            throw new IllegalArgumentException("need gymPrimeKey");
        }
        //Access point of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProblemDatabaseReference = mFirebaseDatabase.getReference().child("problem_data");
        mBelongedDatabaseReference = mFirebaseDatabase.getReference().child("belong_problems")
        .child(mGymPrimeKey);


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
                Intent photoSelectorIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoSelectorIntent.setType("image/*");
                startActivityForResult(photoSelectorIntent, RC_SELECT_PB_PHOTO);
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
            case RC_SELECT_PB_PHOTO:
                if(resultCode == RESULT_OK) {
                    mProblemPhotoUri = returnIntentData.getData();
                    mProblemImageView.setImageBitmap(resizeImage(mProblemPhotoUri));
                }
        }
    }
    public void upLoadPhotoToStorage() {

        //block user interaction on the screen while uploading
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        presentProblemPhotoRef =
                mProblemStorageReference.child(mProblemPhotoUri.getLastPathSegment());
        //Upload file to FirebaseStorage
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

                        Log.v(LOG_TAG, "PrimeKey is : " + mGymPrimeKey);
                        //add problem data to Firebase Database
                        mBelongedDatabaseReference.push().setValue(problem);
                        mProblemDatabaseReference.push().setValue(problem);


                        //DB 의 Ref를 gymPrimeKey 하단에 새로운 problem의 primKey로 만들어야함
                        //ex) problem_data/gymPrimeKey/problemPrimeKey
                        //여기서


                        Toast.makeText(ProblemResister.this, "Problem has been added",Toast.LENGTH_SHORT).show();
                        Log.v(LOG_TAG, "problem photo Uri's String value is " + pPhotoUriString);
                    }
                });
    }

    public Bitmap resizeImage(Uri uri) {
        //TODO : 이미지가 너무작으면 Dialog로 알려주고 필터링 기준은 640 * 480정도?

        Bitmap reSizedBit = null;
        /*Bitmap bit = ((BitmapDrawable) view.getDrawable()).getBitmap();*/
        try{
            Bitmap bit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            int width = bit.getWidth();
            int height = bit.getHeight();
            float rate = 0.0f;

            int maxResolution = 1920;

            int newWidth = width;
            int newHeight = height;

            if(width > height) {
                if(maxResolution < width) {
                    rate = maxResolution/ (float) width;
                    newHeight = (int) (height * rate);
                    newWidth = maxResolution;
                }

            } else {
                if(maxResolution < height) {
                    rate = maxResolution/(float)height;
                    newWidth = (int) (width * rate);
                    newHeight = maxResolution;
                }
            }
            reSizedBit = Bitmap.createScaledBitmap(bit, newWidth, newHeight, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reSizedBit;
    }
}
