package com.example.android.climbtogether.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by MD on 2017-09-22.
 */
//Add Class for clustering the gym markers.
//this will implements ClusterItem
public class GymClusteringMarker implements ClusterItem{
    private final LatLng mPositon;

    public GymClusteringMarker(double Lat, double Lng) {
        mPositon = new LatLng(Lat, Lng);
    }

    @Override
    public LatLng getPosition() {
        return mPositon;
    }
}
