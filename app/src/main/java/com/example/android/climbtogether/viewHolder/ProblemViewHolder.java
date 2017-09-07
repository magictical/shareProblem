package com.example.android.climbtogether.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.climbtogether.Model.Problem;
import com.example.android.climbtogether.R;

/**
 * Created by MD on 2017-08-08.
 */

public class ProblemViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public ProblemViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void bindToProblem(Problem problem) {
        //add ProgressBar
        final ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.problem_progress_bar);

        //Image load by Glide
        ImageView problemImageView = (ImageView) mView.findViewById(R.id.problem_image_view);
        Glide.with(problemImageView.getContext())
                .load(problem.getProblemPhotoUri())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(problemImageView);

        TextView mProblemName = (TextView) mView.findViewById(R.id.text_problem_name);
        mProblemName.setText(problem.getProblemName());

        TextView mProblemLever = (TextView) mView.findViewById(R.id.text_problem_level);
        mProblemLever.setText(problem.getProblemLevel());

        //손볼것
        TextView mProblemDate = (TextView) mView.findViewById(R.id.text_problem_birth_date);
        mProblemDate.setText(problem.getProblemExpireDay());

        //손볼것
        TextView mProblemSubscribers = (TextView) mView.findViewById(R.id.text_problem_subscribers);
        mProblemDate.setText(problem.getProblemFinisher());
    }

    public void bindToDetailProblemData() {}
}
