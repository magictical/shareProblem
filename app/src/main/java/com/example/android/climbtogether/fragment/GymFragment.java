package com.example.android.climbtogether.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.climbtogether.Gym;
import com.example.android.climbtogether.GymAdapter;
import com.example.android.climbtogether.ProblemActivity;
import com.example.android.climbtogether.R;

import java.util.ArrayList;

/**
 * Created by MD on 2017-03-09.
 */

public class GymFragment extends Fragment {

    GymAdapter mGymAdapter;

    //Add Gym button
    Button mGymResister;
    //Add Problem button
    Button mProblemResister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gym_list, container, false);


        //add new Gym ArrayList
        ArrayList<Gym> gym = new ArrayList<Gym>();
        mGymAdapter = new GymAdapter(getActivity(), gym);

        //Bind ListView
        ListView list = (ListView) rootView.findViewById(R.id.gym_list);
        list.setAdapter(mGymAdapter);

        mGymResister = (Button) rootView.findViewById(R.id.resister_gym_button);
        mGymResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent problemIntent = new Intent(getActivity(), ProblemActivity.class);
                startActivity(problemIntent);
            }
        });
        mProblemResister = (Button) rootView.findViewById(R.id.move_to_problem_list);
        mProblemResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProblemActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
