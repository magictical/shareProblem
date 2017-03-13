package com.example.android.climbtogether;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

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
