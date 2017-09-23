package com.example.android.climbtogether.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.climbtogether.Model.Gym;
import com.example.android.climbtogether.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by MD on 2017-02-16.
 */

public class GymResister extends AppCompatActivity {
    //Constant for photo select requestCode
    public static final int RC_SELECT_PHOTO = 100;
    //Constant for photo upload to storage requestCode
    public static final int RC_UPLOAD_PHOTO = 200;
    public static final String LOG_TAG = GymResister.class.getName();

    private String gymName;
    private String gymLocation;
    //필요없어보인다
    //private String gymPhotoUriToString;
    private String gymContact;
    private int gymPrice;

    private EditText mEditGymName;
    private EditText mEditGymLocation;
    private EditText mEditGymContact;
    private EditText mEditGymPrice;
    private ImageView mSelectedImage;

    private Button mAddGymButton;
    //button for select image
    private Button mSelectImageButton;

    //add FireBase Database instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGymDatabaseReference;

    //add Firebase Storage
    FirebaseStorage mFirebaseStorage;
    StorageReference mGymStorageReferences;

    private String gymPhotoUri;

    private Uri mImageUri;

    private Bitmap resizedBit;

    //String for getting putExtra data(User Location)
    public  static final String EXTRA_USER_LOCATION_KEY_LAT = "user_location_key_lat";
    public  static final String EXTRA_USER_LOCATION_KEY_LNG = "user_location_key_lng";

    public static final String USER_LOCATION_KEY = "user_location_key";

    private Location mUserLocation;

    private double mGymLat;
    private double mGymLng;
    private double mGymAlt;

    //accuracy
    private float mProviderAccuracy;
    //bearing
    private float mProviderBearing;
    //provider
    private  String mProviderName;
    //time
    private long mResisteredTime;



    private double mUserLatKey;
    private double mUserLngKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_resister);

        mUserLocation = getIntent().getExtras().getParcelable(USER_LOCATION_KEY);
        if(mUserLocation == null) {
            throw new IllegalArgumentException("Must pass the USER_LOCATION_KEY");
        }
        Toast.makeText(this, "User Location isis :" + mUserLocation.toString(), Toast.LENGTH_LONG).show();




        /*mUserLatKey = getIntent().getDoubleExtra(EXTRA_USER_LOCATION_KEY_LAT, 999);
        mUserLngKey = getIntent().getDoubleExtra(EXTRA_USER_LOCATION_KEY_LNG, 999);
        if(mUserLatKey  == 999 &&  mUserLngKey == 999) {
            throw new IllegalArgumentException("Must pass the EXTRA_USER_LOCATION_KEY");
        }*/
        /*Toast.makeText(this, "User Location is : " + mUserLatKey + "  " + mUserLngKey, Toast.LENGTH_LONG).show();*/


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
        mSelectedImage = (ImageView) findViewById(R.id.resister_gym_photo_image_view);

        //add listener to image button
        mSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoSelectorIntent = new Intent(Intent.ACTION_GET_CONTENT);
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
            }
        });
        getAddressFromLocationData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntentData) {
        super.onActivityResult(requestCode, resultCode, returnIntentData);


        switch(requestCode) {
            case RC_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    mImageUri = returnIntentData.getData();
                    mSelectedImage.setImageURI(mImageUri);
                    resizeImage(mSelectedImage);
                    mSelectedImage.setImageBitmap(resizedBit);
                }
        }
    }

    //get address and put it into the Address Text field
    public void getAddressFromLocationData() {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            mGymLat = mUserLocation.getLatitude();
            mGymLng = mUserLocation.getLongitude();
            mGymAlt = mUserLocation.getAltitude();

            mProviderAccuracy = mUserLocation.getAccuracy();
            mProviderBearing = mUserLocation.getBearing();
            mProviderName = mUserLocation.getProvider();
            mResisteredTime = mUserLocation.getTime();

            List<Address> listAddresses = geocoder.getFromLocation(mGymLat, mGymLng, 5);

            if(listAddresses != null && listAddresses.size() > 0 ) {
                Log.v(LOG_TAG, listAddresses.toString());
                String address = "";

                //도로명
                if(listAddresses.get(0).getSubThoroughfare() != null) {
                    address += listAddresses.get(0).getSubThoroughfare() + ", ";
                }
                //도로명(상위?)
                if(listAddresses.get(0).getThoroughfare() != null) {
                    address += listAddresses.get(0).getThoroughfare() + ", ";
                }
                if(listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality() + ", ";
                }
                if(listAddresses.get(0).getPostalCode() != null) {
                    address += listAddresses.get(0).getPostalCode() + ", ";
                }
                if(listAddresses.get(0).getCountryName() != null) {
                    address += listAddresses.get(0).getCountryName();
                }

                Toast.makeText(this, address, Toast.LENGTH_LONG).show();
                Log.i(LOG_TAG, "result for address geocoding : " + address);
                //300-13, Dangni-dong, Saha-gu, South Korea
                //4, 새동림맨션당리동, 부산광역시, 604-010, 대한민국

                //주소는 geocode 기준으로 변경
                mEditGymLocation.setText(address);
            }
        }catch (IOException e) {
            e.printStackTrace();
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
        StorageReference presentGymPhotoRef =
                mGymStorageReferences.child(mImageUri.getLastPathSegment());
        //Upload file to Firebase Storage
        presentGymPhotoRef.putFile(mImageUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        //save gymPhoto Uri to instance

                        gymPhotoUri = downloadUri.toString();
                        gymName = mEditGymName.getText().toString();
                        gymLocation = mEditGymLocation.getText().toString();
                        gymContact = mEditGymContact.getText().toString();

                /*Log.v(LOG_TAG, "gymPhotoUri is " + gymPhotoUri);
                Log.v(LOG_TAG, "downloadUrl is " + downloadUri.toString());

                gymPhotoUri = downloadUri.toString();*/
                        //upload된 사진의 uri가 사용됨

                        try{
                            gymPrice = Integer.valueOf(mEditGymPrice.getText().toString());

                        } catch (Exception e) {
                            if(mEditGymPrice.getText().toString().equals("") || mSelectedImage.equals("")) {
                                Toast.makeText(GymResister.this, "가격을 입력해주세요(필수)",Toast.LENGTH_SHORT).show();
                            }
                            e.printStackTrace();
                        }
                        //Location, LatLng are included
                        Gym gym = new Gym(gymName, gymLocation, gymContact, gymPrice, gymPhotoUri,
                                mGymLat, mGymLng, mGymAlt, mProviderAccuracy, mProviderBearing,
                                mProviderName, mResisteredTime);
                        mGymDatabaseReference.push().setValue(gym);

                        /*mGymLocationReference.push().setValue(mUserLocation);*/

                        Log.v(LOG_TAG, "gym name is " + gymName + "\n"
                                + "gym Location is " + gymLocation + "\n"
                                + "gym Contact is " + gymContact + "\n"
                                + "gym Price is " + gymPrice + "\n"
                                + "gym PhotoUri is " + gymPhotoUri + "\n"
                                + "gym Lat is " + mGymLat + "\n"
                                + "gym Lng is " + mGymLng + "\n"
                                + "gym Alt is " + mGymAlt
                        );
                        Toast.makeText(getBaseContext(), "gym name is " + gymName + "\n"
                                + "gym Location is " + gymLocation + "\n"
                                + "gym Contact is " + gymContact + "\n"
                                + "gym Price is " + gymPrice + "\n"
                                + "gym PhotoUri is " + gymPhotoUri + "\n"
                                + "gym Lat is " + mGymLat + "\n"
                                + "gym Lng is " + mGymLng + "\n"
                                + "gym Alt is " + mGymAlt, Toast.LENGTH_SHORT).show();

                        Log.v(LOG_TAG, gymName + " " + gymLocation+ " " + gymContact
                                + gymPrice + " "+ gymPhotoUri + " " +  String.valueOf(mGymLat + " "
                                + mGymLng + " " + mGymAlt ));
                    }
                });

    }

    public void resizeImage(ImageView view) {
        //get a Image obj from ImageView

        Bitmap bit = ((BitmapDrawable) view.getDrawable()).getBitmap();

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
        resizedBit = Bitmap.createScaledBitmap(bit, newWidth, newHeight, true);
    }
}
