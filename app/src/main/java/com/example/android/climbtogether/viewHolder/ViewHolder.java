package com.example.android.climbtogether.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.climbtogether.Gym;
import com.example.android.climbtogether.R;
/**
 *    Created by MD on 2017-07-30.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void bindToGym(Gym gym) {


        ImageView mGymImageView = (ImageView) mView.findViewById(R.id.gym_image_view);
        Glide.with(mGymImageView.getContext())
                .load(gym.getGymPhotoUri())
                .into(mGymImageView);



            TextView mGymName = (TextView) mView.findViewById(R.id.text_gym_name);
            mGymName.setText(gym.getGymName());

            TextView mGymLocation = (TextView) mView.findViewById(R.id.text_gym_location);
            mGymLocation.setText(gym.getGymLocation());

            TextView mGymContacts = (TextView) mView.findViewById(R.id.text_gym_contacts);
            mGymContacts.setText(gym.getGymContact());

            TextView mGymPrice = (TextView) mView.findViewById(R.id.text_gym_price);
            mGymPrice.setText(Integer.toString(gym.getGymPrice()));

        }
    }