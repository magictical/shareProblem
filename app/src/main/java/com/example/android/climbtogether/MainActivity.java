package com.example.android.climbtogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;

    private GymAdapter mGymAdapter;

    //add Firebase Auth
    private FirebaseAuth mFirebaseAuth;
    //add Firebase Auth Listener
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    protected FirebaseDatabase mFirebaseDatabase;
    protected DatabaseReference mGymDatabaseReference;

    //child Listener
    protected ChildEventListener mChildEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Access point of DB
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //reference of DB
        mGymDatabaseReference = mFirebaseDatabase.getReference().child("gym_data");

        ArrayList<Gym> gym = new ArrayList<Gym>();
        mGymAdapter = new GymAdapter(this, gym);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    //handle the signed in user
                    //리스트를 업데이트하면 DB에서 Gym data를 가져오는 리스너 Auth후에 DB접근 가능하도록 설정
                    attachDatabaseListener();
                } else {
                    //handle the signed out user
                    //ChildEventListener 제거로 DB에서 접속 불가로만듬
                    //리스트를 업데이트하면 DB에서 Gym data를 가져오는 리스너
                    detachDatabaseListener();
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                        RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Welcome you signed in now!", Toast.LENGTH_SHORT).show();

            }else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "you signed out see you next time", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //리스트가 갱신될때 DB에서 새 data를 업데이트 하기위한 리스너
    //!! 중요 라이프사이클에 Listener를 꺼주는 옵션도 줘야 자원절약
    private void attachDatabaseListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Gym gym = dataSnapshot.getValue(Gym.class);
                    mGymAdapter.add(gym);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mGymDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseListener() {
        if(mChildEventListener != null) {
            mGymDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu :
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
