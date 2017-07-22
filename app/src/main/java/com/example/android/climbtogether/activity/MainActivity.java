package com.example.android.climbtogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.fragment.GymFragment;
import com.example.android.climbtogether.fragment.HomeFragment;
import com.example.android.climbtogether.fragment.ProblemFragment;
import com.example.android.climbtogether.other.CircleTransform;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ////////////////
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://scontent-hkg3-2.xx.fbcdn.net/v/t1.0-9/163777_143173549071592_1853772_n.jpg?oh=32b527c605847e9037d2e5cdab9953a8&oe=59CA65FA";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_FIND_CLIMBING_GYM = "find climbing gym";
    private static final String TAG_FIND_PROBLEMS = "find problems";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private android.os.Handler mHandler;

    private static final int RC_SIGN_IN = 1;

    //add Firebase Auth
    private FirebaseAuth mFirebaseAuth;
    //add Firebase Auth Listener
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Handler 생성(runable에서 사용할듯?)
        mHandler = new android.os.Handler();

        //drawer Layout(프로필화면), nav_view(각목록전체), fab 인스턴스 인스턴스 ref
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //Navigation view header
        navHeader = mNavigationView.getHeaderView(0);
        //메뉴상단의 이름 Ian Kim으로 된 부분
        txtName = (TextView) navHeader.findViewById(R.id.name);
        //웹사이트 주소 적힌 부분
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);

        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        //load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResisterGym = new Intent(MainActivity.this, GymResister.class);
                startActivity(intentResisterGym);
                /*Snackbar.make(v, "replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();*/
            }
        });

        //load nav menu header data
        loadNavHeader();

        //init navigation menu
        setUpNavigationView();

        if(savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        //Firebase Auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //handle the signed in user
                } else {
                    //handle the signed out user
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
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Welcome you signed in now!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "you signed out see you next time", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */

    private  void loadNavHeader() {
        //name, website
        txtName.setText("Ian Kim");
        txtWebsite.setText("www.ClimbTogether.com");

        //loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        //Loading profile image
        Glide.with(this).load(urlProfileImg)
        .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        //showing dot next to notifications label
        mNavigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private  void loadHomeFragment() {
        //selecting appropriate nav menu item
        selectNavMenu();

        //set toolbar title
        setToolbarTitle();

        //if user select the current navigation menu again, don't do anything
        //just close the navigation drawer
        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            mDrawer.closeDrawers();

            //show or hide the fab button
            toggleFab();
            return;
        }

        //Sometimes, when fragment has huge data, screen seems hanging
        //when switching between navigation menus
        //So using runnable, the fragment is loaded with cross fade effect
        //this effect can be seen in Gmail App
        Runnable mPendingRunnable = new Runnable() {
            //fragment 시작될때 효과 적용
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                //플래그먼트로 데이터 보내보기 시험 - 가능함
//                if(fragment instanceof ProblemFragment) {
//                    ((ProblemFragment) fragment).passAuth("i sent", mFirebaseAuth, mAuthStateListener);
//                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        //if mPendingRunnable is not null, then add to the message queue
        if(mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        mDrawer.closeDrawers();

        //reflresh toolbar menu
        invalidateOptionsMenu();
    }
    //경우에 따라 어떤 fragment 를 열지 결정
    private  Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                //open HomeFragment
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:
                //open GymFragment
                GymFragment gymFragment = new GymFragment();
                return gymFragment;

            case 2:
                //open ProblemFragment
                ProblemFragment problemFragment = new ProblemFragment();
                return problemFragment;
            default:
                return new HomeFragment();
        }
    }
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        mNavigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }

        //this code loads home fragment when back key is pressed
        //when user is in other fragment than home
        if(shouldLoadHomeFragOnBackPress) {
            //checking if user is on other navigation menu
            //rather than home
            if(navItemIndex !=0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_find_climbing_gym:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_FIND_CLIMBING_GYM;
                        break;

                    case  R.id.nav_find_problems:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_FIND_PROBLEMS;
                        break;
                    //activity 시작할경우는 아래처럼
    /*                case R.id.nav_about_us:
                        startActivity(new Intent(MainActivity.this, OtherActivity.class));
                        mDrawer.closeDrawers();
                        return true;*/
                    default:
                        navItemIndex = 0;
                }
                //Checking the state of Item is checked or not if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();

                return true;
            }
    });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout(addDrawerListener로 바꿈 원래는 set**)
        mDrawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        if(navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if(navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;

            case R.id.action_mark_all_read:
                Toast.makeText(getApplicationContext(), "All notification marked as read", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_clear_notifications:
                Toast.makeText(getApplicationContext(), "clear all notifications", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //show or hide the fab
    private void toggleFab() {
        if(navItemIndex == 0) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
