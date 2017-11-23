package com.example.android.climbtogether.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.climbtogether.Model.Problem;
import com.example.android.climbtogether.R;
import com.example.android.climbtogether.fragment.ProblemFavorite;
import com.example.android.climbtogether.fragment.ProblemFragment;
import com.example.android.climbtogether.fragment.ProblemInGymFragment;

public class ProblemTabLayoutActivity extends AppCompatActivity {

    //FragmentPagerAdapter variable
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mViewPager;
    //ViewPager variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_tab_layout_acitivity);

        //Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_for_activities);
        setSupportActionBar(toolbar);
        //Add back button on Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Create the adapter that will return a fragment for each section
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new ProblemFragment(),
                    new ProblemInGymFragment(),
                    new ProblemFavorite(),
            };

            //name for the fragments. hard coding for now.
            private  final String[] mFragmentNames = new String[] {
                    getString(R.string.fragment_problem_all),
                    getString(R.string.fragment_problem_my_gym),
                    getString(R.string.fragment_problem_my_favorite),
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        //set up the ViewPager with the sections adapter
        mViewPager = (ViewPager) findViewById(R.id.problem_container);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        //set up for TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.problem_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when back button pressed
            case android.R.id.home :
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
