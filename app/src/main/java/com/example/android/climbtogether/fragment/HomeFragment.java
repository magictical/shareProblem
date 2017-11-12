package com.example.android.climbtogether.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.climbtogether.Model.Gym;
import com.example.android.climbtogether.Model.GymClusteringMarker;
import com.example.android.climbtogether.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

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
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback , ClusterManager.OnClusterItemInfoWindowClickListener<GymClusteringMarker> {

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

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;


    private static Location mCurrentUserLocation;
    private LatLng mCurrentUserLatLng;

    //Add ClusterManager for managing map markers
    private ClusterManager<GymClusteringMarker> mClusterManager;

    private ClusterManager.OnClusterItemInfoWindowClickListener mOnClusterItemInfoWindowClickListener;

    //Add FirebaseDatabase
    private DatabaseReference mGymDbReference;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int LOCATION_ENABLE_MY_LOCATION_CLICK = 2;

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
        Log.d(LOG_TAG, "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView called");

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
        Log.d(LOG_TAG, "onAttach called");
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
        Log.d(LOG_TAG, "onMapReady called and gMap is not null");
        gMap.setOnMyLocationButtonClickListener(this);

        //퍼미션 로직중요!
        //퍼미션 요청 -> onRequestPermissionResult 에서 처리 Fragment 인 경우 ActivityCompat.requestPermission 이 아닌
        //requestPermission 을 사용하는것을 기억하자!!
        //요청 구문뒤 else문은 이미 권한을 받은경우이므로 보통 onRequestPermission 내의 코드와 비슷한경우가 많을것같다.
        //고민 해보면 중복제거할 수 있을것 같은데 일단 패스. 뇌 살살녹는다
        if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ENABLE_MY_LOCATION_CLICK);
        } else {
            if(gMap != null) {
                gMap.setMyLocationEnabled(true);
            }
        }

        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                    if(location == null) {
                        Location tempLocation = new Location("");
                        tempLocation.setLatitude(0);
                        tempLocation.setLongitude(0);
                        Log.d(LOG_TAG, "Error with onLocationChanged");
                        Toast.makeText(getContext(), "onLocationChanged is error", Toast.LENGTH_LONG);
                    }
                    mUserLocationListener.setUserLocation(location);
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    /*gMap.addMarker(new MarkerOptions().position(userLocation).title("I'm here(new Position"));*/
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                    Log.i(LOG_TAG, "location Listener : Location Changed" + location.toString());
                } catch (Exception e) {
                    //이메세지가 나올경우 현재위치가 정확하지 않을 수 있으므로 해당 내용을 알려주는게 좋을것 같다.
                    Log.e(LOG_TAG, "location error " + e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            //Provider의 상태에 따라서 사용할 프로바이더와 에러메세지 출력
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        //마찬가지 권한이 없읋경우 onRequestResult에서 최초동작을 모두 실행해야 되므로 권한이 있을경우인 Else의 경우에도
        //동일하게 모든동작을 기술해야 정상적으로 작동한다. 여기서 코드 중복이 굉장히 많이 발생하는데 아마 메서드로 모아서 써야할것 같다.일단 여기까지!!
        } else {
            initLocationProvider();
            Log.v(LOG_TAG, "mCurrentLocation is : " + mCurrentUserLocation.toString());
            mCurrentUserLatLng = new LatLng(mCurrentUserLocation.getLatitude(), mCurrentUserLocation.getLongitude());
            //mCurrentLocation null에서 값을 추가해주고  UserLocationListener 인터페이스의 메서드 실행
            mUserLocationListener.setUserLocation(mCurrentUserLocation);

        /*gMap.addMarker(new MarkerOptions().position(mCurrentUserLatLng).title("I'm here"));*/
            CameraPosition position = CameraPosition.builder().target(mCurrentUserLatLng).zoom(12).build();
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        if(gMap != null) {
            loadMapMarkers();
            Log.d(LOG_TAG, "loaded from MapReady 252");
        }
    }

    //if return = false => Default behavior : move to User's current location
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "Move to My Location", Toast.LENGTH_LONG).show();
        return false;
    }

    //when request permission is triggered then check the permission and init setMyLocationEnabled
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        initLocationProvider();

                        mCurrentUserLatLng = new LatLng(mCurrentUserLocation.getLatitude(), mCurrentUserLocation.getLongitude());
                        //mCurrentLocation null에서 값을 추가해주고  UserLocationListener 인터페이스의 메서드 실행
                        mUserLocationListener.setUserLocation(mCurrentUserLocation);
                    }
                }
                break;
            //최초 권한 받을때 MyLocation 버튼을 활성화
            case LOCATION_ENABLE_MY_LOCATION_CLICK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if(gMap != null) {
                            gMap.setMyLocationEnabled(true);
                        }
                    }
                }
                break;
        }
    }
    //Init LocationProviders
    //check permission for location and Select Provider based on its condition
    //and save the location info to mCurrentLocation variable
    public void initLocationProvider() {
        int MIN_TIME_BW_UPDATES = 10000;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        Location initLocation = null;
        int checkPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        try {
            if(checkPermission == PackageManager.PERMISSION_GRANTED) {
                boolean isGPSEnabled = mLocationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                boolean isNetworkEnabled = mLocationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGPSEnabled || isNetworkEnabled) {

                    // if all providers are enable then use GPS
                    if (isGPSEnabled) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d("All Provider", "Use best provider : GPS");
                        if (mLocationManager != null) {
                            initLocation = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        //initLocation == null 일경우 GPS신호에 이상이 있음(안잡힐경우가 이런경우가 많음)
                        //NetWork 사용
                        if(initLocation == null) {

                            mLocationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    mLocationListener);
                            Log.d(LOG_TAG, "GPS error, Use Network");

                            initLocation = mLocationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }else {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d("Network", "Network Enabled");
                        if (mLocationManager != null) {
                            initLocation = mLocationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                } else {
                    //if all providers are unable, then initLocation equals to null
                    // in this case, use PassiveProvider(not accurate)
                    // and notifying user It's not accurate.
                    if(initLocation == null) {
                        if (mLocationManager
                                .isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.PASSIVE_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    mLocationListener);
                            Log.d("Passive", "Passive is Enabled");
                            if (mLocationManager != null) {
                                initLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                                Toast.makeText(getContext(), "Can't find location this might be inaccurate Please turn on GPS, Network", Toast.LENGTH_LONG);
                                Log.d(LOG_TAG, "use passive provider");
                            }
                            //if all providers are unable, then set default location and notifying it.
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }
        //PASSIVE도 Error인경우에서 initLocation인 null일때 nullPointException 방지 + 메세지
        if (initLocation == null) {
            initLocation = new Location("");
            initLocation.setLatitude(0);
            initLocation.setLatitude(0);
            Toast.makeText(getContext(), "can't find user location, Please check GPS, Network", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "can't use Any Provider");
        }
        mCurrentUserLocation = initLocation;
    }

    public boolean checkLocationPermission() {
        boolean isPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            isPermissionGranted = true;
        }else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        }
        return isPermissionGranted;
    }
    //This Method Manage all Location of the Gyms based on its location through FirebaseDatabase
    //and if gyms density is high, it will use Map clustering
    public void loadMapMarkers() {
        if(gMap != null) {

            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((float) mCurrentUserLocation.getLatitude(),
                    (float)mCurrentUserLocation.getLongitude()), 10));

            mClusterManager = new ClusterManager<GymClusteringMarker>(getContext(), gMap);

            //Sets Renderer for Change MapMaker's icon
            mClusterManager.setRenderer(new PersonRenderer());
            //setOnCameraIdleListener : Sets a callback that is invoked when camera movement has ended.
            gMap.setOnCameraIdleListener(mClusterManager);
            //A List for containing GymClusteringMarkers
            final List<GymClusteringMarker> clusterList = new ArrayList<GymClusteringMarker>();

            gMap.setOnMarkerClickListener(mClusterManager);
            //set Window Adapter
            gMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            gMap.setOnInfoWindowClickListener(mClusterManager);
            //리스너를 어디에서 초기화?
            /*mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<GymClusteringMarker>() {
                @Override
                public void onClusterItemInfoWindowClick(GymClusteringMarker gymClusteringMarker) {
                    //deal with it
                }
            });

            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<GymClusteringMarker>() {
                @Override
                public boolean onClusterItemClick(GymClusteringMarker gymClusteringMarker) {
                    //get result when it clicked
                    return false;
                }
            });*/

            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                    new MyCustomAdapterForItems());
            ////////////////////

            mGymDbReference = FirebaseDatabase.getInstance().getReference().child("gym_data");
            mGymDbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //get all data in FireBase's DB and save it into the clusterList
                    for(DataSnapshot c : dataSnapshot.getChildren()) {
                        Gym gymData = c.getValue(Gym.class);
                        Log.d(LOG_TAG, gymData.getGymName().toString());
                        c.getRef();
                        Log.d(LOG_TAG, "this is the result of getKey : " + c);

                        //no need to use marker anymore! use ClusterManager instead.
                        /*LatLng marker = new LatLng(mGymData.getGymLat(), mGymData.getGymLng());
                        String markerName = mGymData.getGymName();
                        gMap.addMarker(new MarkerOptions().position(marker).title(markerName));
                        Log.d(LOG_TAG, markerName);*/

                        GymClusteringMarker gymCluster = new GymClusteringMarker(gymData.getGymName(), gymData.getGymName(), gymData.getGymPhotoUri(), gymData.getGymLat(), gymData.getGymLng());
                        clusterList.add(gymCluster);
                    }
                    //add it to manager
                    mClusterManager.addItems(clusterList);
                    //execute clustering!
                    mClusterManager.cluster();
                }
                //클래스를 직접 불러와서 for 문돌리기랑 비교교
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(GymClusteringMarker gymClusteringMarker) {

    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getActivity().getLayoutInflater().inflate(
                    R.layout.info_window, null);
        }
        //ImageView를 view_id로 지정해서 바꿀 수있지않나?
        @Override
        public View getInfoWindow(Marker marker) {

            Log.d(LOG_TAG, "marker see" + marker.getTitle());


            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.txtTitle));
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.txtSnippet));

            tvTitle.setText(marker.getTitle());
            tvSnippet.setText(marker.getTitle());


            //info윈도우에 이미지 가져오기 연습
            ImageView cImg = (ImageView) myContentsView.findViewById(R.id.img_view);

            return myContentsView;

        }
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<GymClusteringMarker> {
        private final IconGenerator mIconGenerator = new IconGenerator(getContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getContext());
        private ImageView mImageView;
        private TextView mTextView;
        private final ImageView mClusterImageView;
        private final int mXcoordDimension;
        private final int mYcoordDimension;

        public PersonRenderer() {
            super(getApplicationContext(), gMap, mClusterManager);

            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.multi_profile, null);
            //Use this when customize cluster icon
            /*mClusterIconGenerator.setContentView(multiProfile);*/
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mTextView = new TextView(getContext());
            mXcoordDimension = (int) getResources().getDimension(R.dimen.custom_profile_image_Xcoord);
            mYcoordDimension = (int) getResources().getDimension(R.dimen.custom_profile_image_Ycoord);
            mTextView.setLayoutParams(new ViewGroup.LayoutParams(mXcoordDimension, mYcoordDimension));

            //지도에 표시될이름에 TV에 사이즈맞춰서 표시되도록
            mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mTextView.setPadding(padding, padding, padding, padding);
            mTextView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mTextView);
        }

        /*문제점
        * 지금 맵을 확대면 클러스터에서 아이템으로 전환되면서 하단의 메서드를 호출하는데
        * uri에서 이미지를 불러와서 비트맵으로 변환하는 과정이 시간이 조금 필요한데
        * 렌더링에서는 이미지 로딩이 끝나기도 전에 메서드를 필요수 만큼 호출해버려서 비트맵 변수의
        * 값이 싱크가 안맞게 되어버린다.
        *
        * 로딩이 끝날떄까지 기다리게하는 리스너가 필요할듯하다.
        *
        * */
        // 이거 안쓸거같은데 일단 지켜보자,../////////////////////////
       /* @Override
        protected void onClusterItemRendered(GymClusteringMarker gymClusteringMarker, Marker marker) {
            Log.v(LOG_TAG, "onClusterItemRendered called");
            Glide
                    .with(getContext())
                    .load(gymClusteringMarker.getGymImgUri())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            if (resource != null) {
                                Bitmap bmp = resource; // Possibly runOnUiThread()
                                mImageView.setImageBitmap(bmp);

                            }

                        }
                    });

            Bitmap icon = mIconGenerator.makeIcon();

        }*/

        @Override
        protected void onBeforeClusterItemRendered(GymClusteringMarker gymClusteringMarker, MarkerOptions markerOptions) {
            //shows name of Gyms
            Log.v(LOG_TAG, "onBeforeCluster called");
            String name = gymClusteringMarker.getName();
            if(name.length() >= 16) {
                name =  name.substring(0, 15) + "...";
            }
            mTextView.setTextSize(15);
            mTextView.setText(name);

            //텍스트 옵션 필요한 부분
            // 1. 글자수 많으면 ...으로표시하기
            // 2. 글스타일 지정?
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(gymClusteringMarker.getTitle());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<GymClusteringMarker> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            /*List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;*/

            /*for (GymClusteringMarker p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = null;
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);*/

            /*mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));*/
            /*markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));*/
        }

        @Override
        protected boolean shouldRenderAsCluster (Cluster cluster){
            // Always render clusters.
            return cluster.getSize() > 1;
        }

    }





    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart called");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume called");

        if(gMap == null) {
            Log.d(LOG_TAG, "gMap is null");
        } else {
            loadMapMarkers();
            Log.d(LOG_TAG, "gMpa has values" + gMap.toString());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause called");
    }

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
