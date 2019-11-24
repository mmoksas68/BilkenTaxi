package com.example.fatihpc.bilkentransportation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationOfLandmarkActivity extends FragmentActivity implements OnMapReadyCallback
{
    // properties.
    private GoogleMap mMap;

    /**
     * arranges the settings.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_of_landmark);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // getting the landmark data.
        double latitude  = getIntent().getDoubleExtra( "latitude"  , 0 );
        double longitude = getIntent().getDoubleExtra( "longitude" , 0 );
        String locationName = getIntent().getStringExtra( "locationName" );

        // Adding a marker to the landmark and moving the camera
        LatLng locationOfLandmark = new LatLng( latitude , longitude );
        mMap.addMarker(new MarkerOptions().position( locationOfLandmark ).title( locationName ));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( locationOfLandmark , 15 ) );
    }

    /**
     * terminates the intent
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

}