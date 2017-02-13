package com.example.android.climbtogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.id.list;

/**
 * Created by MD on 2017-02-14.
 */

public class GymAdapter extends ArrayAdapter<Gym> {
    private Context saveContext;

    //커스텀 생성자 일단은 gym만 받아서 상위생성자 호출하자
    public GymAdapter (Context context, ArrayList<Gym> gym) {
        super(context, 0, gym);
        saveContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listScraps = convertView;
        if(listScraps == null) {
            listScraps = LayoutInflater.from(getContext()).inflate(R.layout.listitem_in_gym, parent, false);
        }
        final Gym currentGym = getItem(position);

        //set Gym photo
        ImageView gymImage = (ImageView) listScraps.findViewById(R.id.gym_image_view);
        gymImage.setImageResource(currentGym.getGymPhotoResourceId());

        //set Gym name
        TextView gymName = (TextView) listScraps.findViewById(R.id.text_gym_name);
        gymName.setText(currentGym.getGymName());

        TextView gymLocation = (TextView) listScraps.findViewById(R.id.text_gym_location);
        gymLocation.setText(currentGym.getGymLocation());

        //set Gym contacts
        TextView gymContacts = (TextView) listScraps.findViewById(R.id.text_gym_contacts);
        gymContacts.setText(currentGym.getGymContact());

        //set Gym pirce
        TextView gymPrice = (TextView) listScraps.findViewById(R.id.text_gym_price);
        gymPrice.setText(String.valueOf(currentGym.getGymPrice()));

        return listScraps;
    }
}
