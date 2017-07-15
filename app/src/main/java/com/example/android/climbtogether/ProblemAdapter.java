package com.example.android.climbtogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.R.id.list;

/**
 * Created by MD on 2017-03-01.
 */

public class ProblemAdapter extends ArrayAdapter<Problem> {
    private Context saveContext;

    //ProblemFragment Context를 받아서 생성자에게 전달
    public ProblemAdapter(Context context, ArrayList<Problem> problem) {
        super(context, 0, problem);
        saveContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listScraps = convertView;
        if(listScraps == null) {
            listScraps = LayoutInflater.from(getContext()).inflate(R.layout.listitem_problem, parent, false);
        }

        final Problem currentProblem = getItem(position);

        //set Problem photo
        ImageView problemImage = (ImageView) listScraps.findViewById(R.id.problem_image_view);
        Glide.with(problemImage.getContext())
                .load(currentProblem.getProblemPhotoUri())
                .into(problemImage);

        //set Problem name
        TextView problemName = (TextView) listScraps.findViewById(R.id.text_problem_name);
        problemName.setText(currentProblem.getProblemName());

        //set Problem Level
        TextView problemlevel = (TextView) listScraps.findViewById(R.id.text_problem_level);
        problemlevel.setText(currentProblem.getProblemLevel());

        //set Problem creator
        TextView problemCreator = (TextView) listScraps.findViewById(R.id.text_problem_creator);
        problemCreator.setText(currentProblem.getProblemCreator());

        //set Problem finisher list
        TextView problemFinisher = (TextView) listScraps.findViewById(R.id.text_problem_finisher_list);
        problemFinisher.setText(currentProblem.getProblemFinisher());

        //set Problem Challenger list
        TextView problemChallenger = (TextView) listScraps.findViewById(R.id.text_problem_challenger_list);
        problemChallenger.setText(currentProblem.getProblemChallenger());

        //set Problem expireDay
        TextView problemExpireDay = (TextView) listScraps.findViewById(R.id.text_problem_expireday);
        problemExpireDay.setText(currentProblem.getProblemExpireDay());

        return listScraps;
    }
}
