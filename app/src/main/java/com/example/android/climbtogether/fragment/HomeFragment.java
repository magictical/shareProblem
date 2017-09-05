package com.example.android.climbtogether.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.climbtogether.PermissionUtils;
import com.example.android.climbtogether.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String LOG_TAG = HomeFragment.class.getName();
    //Interface for passing Geo Data to Activity
    public interface UserLocationListener {
        void setUserLocation(Location userLocation);
    }

    UserLocationListener mUserLocationListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //use google maps
    private GoogleMap gMap;

    private GoogleMap.OnMyLocationButtonClickListener mOnMyLocationButtonClickListener;


    private LocationManager mLocationManager;
    private LocationListener mLocationListener;


    private static Location mCurrentUserLocation;
    private LatLng mCurrentUserLatLng;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}
     */
    private boolean mPermissionDenied = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.home_map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserLocationListener) {
            mUserLocationListener = (UserLocationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement GeoDataListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Detach Listeners
        mListener = null;
        mUserLocationListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setOnMyLocationButtonClickListener(this);

        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                /*mCurrentUserLocation = location;*/
                mUserLocationListener.setUserLocation(location);
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(userLocation).title("I'm here(new Position"));
                gMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                Log.i("Location : ", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            /*if(lastKnownLocation == null) {
                lastKnownLocation = new Location(LocationManager.GPS_PROVIDER);

                lastKnownLocation.setLatitude(36);
                lastKnownLocation.setLongitude(128);
            }*/
            Log.v(LOG_TAG, lastKnownLocation.toString());
            mCurrentUserLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            //mCurrentLocation null에서 값을 추가해주고  UserLocationListener 인터페이스의 메서드 실행
            mUserLocationListener.setUserLocation(lastKnownLocation);


            gMap.clear();

            gMap.addMarker(new MarkerOptions().position(mCurrentUserLatLng).title("I'm here"));
            CameraPosition position = CameraPosition.builder().target(mCurrentUserLatLng).zoom(12).build();
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));


        } else {

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

            } else {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);

                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //mCurrentLocation null에서 값을 추가해주고  UserLocationListener 인터페이스의 메서드 실행
                mUserLocationListener.setUserLocation(lastKnownLocation);

                mCurrentUserLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                gMap.clear();

                gMap.addMarker(new MarkerOptions().position(mCurrentUserLatLng).title("I'm here"));
                CameraPosition position = CameraPosition.builder().target(mCurrentUserLatLng).zoom(12).build();
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

            }
        }
        /*if(mCurrentUserLocation == null) {
            if (Build.VERSION.SDK_INT < 23) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                mCurrentUserLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                *//*mUserLocationListener.setUserLocation(mCurrentUserLocation);*//*
            }else {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }else {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                    Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mUserLocationListener.setUserLocation(location);
                }
            }
        }*//* else {
            mUserLocationListener.setUserLocation(mCurrentUserLocation);
        }*/
    }
    /**
     * Enable the My Location layer if the fine location permission has been granted
     */
    private void enableMyLocation() {
        if (Build.VERSION.SDK_INT < 23) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Log.v(LOG_TAG, lastKnownLocation.toString());
                mCurrentUserLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                gMap.clear();

                gMap.addMarker(new MarkerOptions().position(mCurrentUserLatLng).title("I'm here"));
                CameraPosition position = CameraPosition.builder().target(mCurrentUserLatLng).zoom(12).build();
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));


            //mCurrentLocation null에서 값을 추가해주고  UserLocationListener 인터페이스의 메서드 실행
            /*mUserLocationListener.setUserLocation(lastKnownLocation);*/
        } else {

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

            } else {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);

                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //mCurrentLocation null에서 값을 추가해주고  UserLocationListener 인터페이스의 메서드 실행
                mUserLocationListener.setUserLocation(lastKnownLocation);

                mCurrentUserLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                gMap.clear();

                gMap.addMarker(new MarkerOptions().position(mCurrentUserLatLng).title("I'm here"));
                CameraPosition position = CameraPosition.builder().target(mCurrentUserLatLng).zoom(12).build();
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

            }
        }
        /*if (gMap != null){
            //Access to the location has been granted to the app
            gMap.setMyLocationEnabled(true);
        }*/

    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button click", Toast.LENGTH_LONG).show();
       /* CameraPosition position = CameraPosition.builder().target(mCurrentUserLatLng).zoom(12).build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));*/


        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                }

            }
            if (gMap != null) {
                gMap.setMyLocationEnabled(true);
            }
        }
    }
   /* public Location getLocation() {
        int MIN_TIME_BW_UPDATES = 10000;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isPassiveEnabled = locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled || isPassiveEnabled) {

                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                if (isPassiveEnabled && location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                }

                if (isNetworkEnabled && location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }*/



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
