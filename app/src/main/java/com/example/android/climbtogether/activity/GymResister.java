package com.example.android.climbtogether.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.climbtogether.Model.Gym;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.fragment.DatePickerFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by MD on 2017-02-16.
 */

public class GymResister extends AppCompatActivity implements DatePickerFragment.DateDialogListener {
    //Constant for photo select requestCode
    public static final int RC_SELECT_PHOTO = 100;
    //Constant for photo upload to storage requestCode
    public static final int RC_UPLOAD_PHOTO = 200;
    public static final String LOG_TAG = GymResister.class.getName();

    //tag for DateDialog
    private static final String DIALOG_DATE = "GymResister.DateDialog";

    private Toolbar mToolbar;

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
    //button for change Date
    private Button mChgDateButton;
    //EditView for Date and time
    private EditText mEditDatenTime;

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

        //Add Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_for_activities);
        setSupportActionBar(mToolbar);
        //Add back button on Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //button for change the Date
        mChgDateButton = (Button) findViewById(R.id.resister_change_date);
        //editText for time and date
        mEditDatenTime = (EditText) findViewById(R.id.resister_gym_date);
        Date currentD = Calendar.getInstance().getTime();
        mEditDatenTime.setText(dateToString(currentD));

        mChgDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialogDatePickerFragment = new DatePickerFragment();
                dialogDatePickerFragment.show(getFragmentManager(), DIALOG_DATE);
            }
        });



        getAddressFromLocationData();

        //add listener to image button
        mSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoSelectorIntent = new Intent();
                photoSelectorIntent.setType("image/*");
                photoSelectorIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(photoSelectorIntent, "Select Picture"), RC_SELECT_PHOTO);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Add upload button on the Toolbar
            case R.id.action_upload_data:
                uploadPhotoToStorage();
                return true;
            //when back button pressed
            case android.R.id.home :
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntentData) {
        super.onActivityResult(requestCode, resultCode, returnIntentData);

        switch(requestCode) {
            case RC_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    mImageUri = returnIntentData.getData();
                    /*mSelectedImage.setImageURI(mImageUri);*/
                    mSelectedImage.setImageBitmap(resizeImage(mImageUri));
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

        //block user interaction on the screen while uploading
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(mImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference presentGymPhotoRef =
                    mGymStorageReferences.child(mImageUri.getLastPathSegment());
            //Upload file to Firebase Storage
            //TODO : 현재 업로드되는 파일은 리사이즈된것이 아닌 URI에서 ref된 파일 그 자체라 아직은
            // Resize하는 의미가 UI에 표시되는것 말고는 없다 이거 수정해야됨
            presentGymPhotoRef.putFile(mImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //if upload is successful, hide progress bar
                            Uri downloadUri = taskSnapshot.getDownloadUrl();

                            //save gymPhoto Uri to instance
                            gymPhotoUri = downloadUri.toString();
                            gymName = mEditGymName.getText().toString();
                            gymLocation = mEditGymLocation.getText().toString();
                            gymContact = mEditGymContact.getText().toString();

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

                            progressDialog.dismiss();
                            //move to home fragment

                            //get back user interaction on screen
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            //TODO : add function for move to homeFragment
                            Intent intent = new Intent(GymResister.this, MainActivity.class);
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //if failure with upload task
                            //hide the progressbar
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            //get back user interaction on screen
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = ((100.0) * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
    }

    @Override
    public void onFinishDialog(Date date) {
        mEditDatenTime.setText(dateToString(date));

    }

    public String dateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String convertedDate = simpleDateFormat.format(date);
        //좀있다가 할거는 Date resister edit 에서
        return convertedDate;
    }

    public String dateToStringDetail(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String convertedDate = simpleDateFormat.format(date);
        return convertedDate;
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
