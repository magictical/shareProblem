package com.example.android.climbtogether;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

/**
 * Created by MD on 2017-02-16.
 */

public class GymResister extends Activity {
    //Constant for photo select requestCode
    public static final int RC_SELECT_PHOTO = 100;
    //Constant for photo upload to storage requestCode
    public static final int RC_UPLOAD_PHOTO = 200;
    public static final String LOG_TAG = GymResister.class.getName();

    private String gymName;
    private String gymLocation;
    private String gymPhotoUriToString;
    private String gymContact;
    private int gymPrice;

    private EditText mEditGymName;
    private EditText mEditGymLocation;
    private EditText mEditGymContact;
    private EditText mEditGymPrice;

    private ImageView mPhotoResource;


    private Button mAddGymButton;
    //button for select image
    private Button mSelectimage;

    //add FireBase Database instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGymDatabaseReference;

    //add Firebase Storage
    FirebaseStorage mFirebaseStorage;
    StorageReference mGymStorageReferences;

    private StorageReference presentGymPhotoRef;

    private String gymPhotoUri;
    private Uri selectedImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_resister);

        //Access point of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Access point of storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        //A reference to get a specific part of database
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");
        //A reference to get the gym photos data in Firebase Storage.
        mGymStorageReferences = mFirebaseStorage.getReference().child("gym_photos");




        mEditGymName = (EditText) findViewById(R.id.resister_gym_name);
        /*mEditGymName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                //ACTION중 SEND가 실행때 아래코드가 실행됨
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    gymName = mEditGymName.getText().toString();
                }
                return handled;
            }
        });*/

        //각 U.I를 인스턴스와 연결함
        mEditGymLocation = (EditText) findViewById(R.id.resister_gym_location);
        mEditGymContact = (EditText) findViewById(R.id.resister_gym_contact);
        mEditGymPrice = (EditText) findViewById(R.id.resister_gym_price);

        //select img from sd card using by Intent and display img
        mPhotoResource = (ImageView) findViewById(R.id.resister_gym_photo);
        mSelectimage = (Button) findViewById(R.id.resister_gym_load_image_button);
        mSelectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoSelectorIntent = new Intent(Intent.ACTION_PICK);
                photoSelectorIntent.setType("image/*");
                startActivityForResult(photoSelectorIntent, RC_SELECT_PHOTO);
            }
        });

        //Gym Add 버튼 바인딩
        mAddGymButton = (Button)findViewById(R.id.add_gym);
        mAddGymButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add 버튼을 누르면 업로드를 하고 업로드된 사진의 uri를 갱신함
                uploadPhotoToStorage();

                gymName = mEditGymName.getText().toString();
                gymLocation = mEditGymLocation.getText().toString();
                gymContact = mEditGymContact.getText().toString();
                //upload된 사진의 uri가 사용됨
                gymPhotoUriToString = gymPhotoUri;

                try{
                    gymPrice = Integer.valueOf(mEditGymPrice.getText().toString());

                    /*gymPhotoUriToString = Integer.parseInt(mPhotoResource.getText().toString());*/
                } catch (Exception e) {
                    if(mEditGymPrice.getText().toString().equals("") || mPhotoResource.equals("")) {
                        Toast.makeText(GymResister.this, "가격을 입력해주세요(필수)",Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
                Gym gym = new Gym(gymName, gymLocation, gymPhotoUriToString, gymContact, gymPrice);
                mGymDatabaseReference.push().setValue(gym);

                    Log.v(LOG_TAG, "gym name is " + gymName + "\n"
                            + "gym Location is" + gymLocation + "\n"
                            + "gym Contact is" + gymContact + "\n"
                            + "gym Price is " + gymPrice + "\n"
                            + "gym Photo is not ready" + gymPhotoUriToString);

                    Toast.makeText(GymResister.this, gymName + " " + gymLocation+ " " + gymContact
                            + gymPrice+ " "+ gymPhotoUriToString, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntentData) {
        super.onActivityResult(requestCode, resultCode, returnIntentData);


        switch(requestCode) {
            case RC_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {

                    selectedImgUri = returnIntentData.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImgUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap yourselectedImage = BitmapFactory.decodeStream(imageStream);
                    mPhotoResource.setImageURI(selectedImgUri);
                }
        }
    }
    //delete data from storage
    public void deleteOutDatedPhotoInStorage(StorageReference previousRef) {
        previousRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(LOG_TAG, "file has deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(LOG_TAG, "oops! there is some error");
            }
        });
    }

    public void uploadPhotoToStorage() {
        //현재 선택한 photo의 ref
        presentGymPhotoRef =
                mGymStorageReferences.child(selectedImgUri.getLastPathSegment());
        //Upload file to Firebase Storage
        presentGymPhotoRef.putFile(selectedImgUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        //save gymPhoto Uri to instance
                        gymPhotoUri = downloadUri.toString();
                        Log.v(LOG_TAG, "gymPhotoUriToString Value is " + gymPhotoUriToString);
                    }
                });
    }
}
