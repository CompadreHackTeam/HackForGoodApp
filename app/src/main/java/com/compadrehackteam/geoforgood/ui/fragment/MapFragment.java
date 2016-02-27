package com.compadrehackteam.geoforgood.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.compadrehackteam.geoforgood.R;
import com.compadrehackteam.geoforgood.manager.GeofenceManager;
import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * This MapFragment lets you see all Runners in the map
 */
public class MapFragment extends Fragment {

    /**
     * View with the Google Map.
     */
    private MapView mMapView;
    /**
     * Google Map.
     */
    private GoogleMap mGoogleMap;
    /**
     * The circle to show the geofences
     */
    private Circle mCircle;
    /**
     * The real user location
     */
    private Location mUserLocation;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    /**
     * The circle list
     */
    private List<Circle> mCircleList;

    /**
     * Permission state for map
     * TODO : UNHARDCODE
     */
    private Boolean mPermission = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // List init
        mCircleList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // If the user dont let Location permission, show no permission fragment
        if (!mPermission) {
            return inflater.inflate(R.layout.fragment_map_no_permission, container, false);
        } else {
            // If the user given the permissions, show normal map
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            ButterKnife.bind(this,rootView);
            fab.setBackgroundColor(getResources().getColor(R.color.colorButton));
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);

            // Gets to GoogleMap from the MapView and does initialization stuff
            mMapView.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.474, -6.370961), 14.0f));
                    mGoogleMap.setMyLocationEnabled(true);
                    drawGeofences();
                }
                // DO FUNNY THINGS HERE
            });

            setHasOptionsMenu(true);
            return rootView;
        }
    }

    @OnClick(R.id.fab)
    public void downloadAndDraw(){

        GeofenceManager.downloadGeofencesFromServer(new GeofenceManager.DownloadListener() {
            @Override
            public void onDownloadSuccess(int size) {
                Snackbar.make(getActivity().getCurrentFocus(),"Has descargado "+size +" Geofences",Snackbar.LENGTH_LONG).show();
                drawGeofences();
            }
            @Override
            public void onDownloadError(String error) {
                Snackbar.make(getActivity().getCurrentFocus(),"Error descargando Geofences",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     *  Draw the geofences on map
     */
    public void drawGeofences(){

        setGeofencesOnMap(GeofenceManager.getAllGeofencesFromDatabase());
    }

    /**
     * This method draw the user location on the map
     */
    public void setGeofencesOnMap(List<GeofenceObject> nimbeesGeofenceList) {

        for (GeofenceObject geofence : nimbeesGeofenceList) {
            mCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(geofence.getLatitude(), geofence.getLongitude()))
                    .radius(geofence.getRadius())
                    .strokeWidth(0f)
                    .fillColor(Color.argb(255, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)))); // Random color, more fun
            mCircleList.add(mCircle);
        }
    }

    /**
     * This method stop the previous added NimbeesGeoFence objects in the service
     */
    public void removeGeofencesFromMap() {

        // GeofenceManager.disableGeofencesInService();
        for (Circle c : mCircleList) {
            c.remove();
        }
        mCircleList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermission) {
            mMapView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPermission) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mPermission) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

}

