package com.example.android.climbtogether.Model;

import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by MD on 2017-09-22.*/


//Add Class for clustering the gym markers.
//this will implements ClusterItem
public class GymClusteringMarker implements ClusterItem  {
    private final LatLng mPosition;
    private String mName;
    private String mContent;
    private String mGymImgUri;

    public GymClusteringMarker(String title, String content, String imgUri, double Lat, double Lng) {
        mName = title;
        mContent = content;
        mGymImgUri = imgUri;
        mPosition = new LatLng(Lat, Lng);
    }

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }

    public String getGymImgUri() {
        return mGymImgUri;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mName;
    }

    @Override
    public String getSnippet() {
        return mGymImgUri;
    }
}
