package com.example.fatihpc.bilkentransportation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ShowVehicleActivity extends FragmentActivity
        implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener
{
    // properties
    private GoogleMap mMap;
    private LocationManager   locationManager;
    private LocationListener locationListener;

    /**
     * sets the map.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vehicle);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setLocationManager();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        // setting google map and adding marker listener.
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // Add a marker to passenger.
        ParseGeoPoint parseGeoPoint = ParseUser.getCurrentUser().getParseGeoPoint( "location" );
        double latitude  = parseGeoPoint.getLatitude();
        double longitude = parseGeoPoint.getLongitude();

        LatLng passengerLatLng = new LatLng( latitude , longitude);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_GREEN );
        mMap.addMarker( new MarkerOptions().position(passengerLatLng).title("Your Location").icon( bitmapDescriptor )).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(passengerLatLng));
    }

    /**
     * sets location manager and location listener.
     */
    public void setLocationManager()
    {
        locationManager = (LocationManager) this.getSystemService( LOCATION_SERVICE );
        locationListener = new LocationListener()
        {
            /**
             * sets the current location to the server and
             * trying to find nearby drivers for passenger.
             * @param location is the updated location.
             */
            @Override
            public void onLocationChanged(Location location)
            {
                // clearing maps
                mMap.clear();

                // updating current users location.
                double latitude  = location.getLatitude();
                double longitude = location.getLongitude();
                ParseGeoPoint parseGeoPoint = new ParseGeoPoint( latitude , longitude );
                ParseUser.getCurrentUser().put( "location" , parseGeoPoint );
                ParseUser.getCurrentUser().saveInBackground();

                // adding a marker to current users location.
                LatLng passengerLatLng = new LatLng( latitude , longitude );
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_GREEN );
                mMap.addMarker( new MarkerOptions().position(passengerLatLng).title( "Your Location" ).icon(bitmapDescriptor));

                // indicating the nearby drivers.
                ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                parseQuery.whereEqualTo( "driverOrPassenger" , "driver" );
                parseQuery.setLimit( 5 );
                parseQuery.whereNear( "location" , parseGeoPoint );
                List<String> blockedUsers = ParseUser.getCurrentUser().getList("blockedUsers");
                parseQuery.whereNotContainedIn( "objectId" , blockedUsers );
                parseQuery.findInBackground(new FindCallback<ParseUser>()
                {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e)
                    {
                        if ( e == null )
                        {
                            if ( objects.size() > 0 )
                            {
                                for ( final ParseUser parseUser : objects )
                                {
                                    System.out.println( "CODE PASSED HERE" );

                                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                                    String currentUserId = ParseUser.getCurrentUser().getObjectId();
                                    List<String> list = parseUser.getList( "blockedUsers" );
                                    query.whereNotContainedIn( currentUserId , list );
                                    query.findInBackground(new FindCallback<ParseUser>()
                                    {
                                        @Override
                                        public void done(List<ParseUser> objects, ParseException e)
                                        {
                                            // getting location of the user
                                            ParseGeoPoint parseGeoPoint = parseUser.getParseGeoPoint( "location" );
                                            double latitude  = parseGeoPoint.getLatitude();
                                            double longitude = parseGeoPoint.getLongitude();

                                            // adding a new marker for the user.
                                            LatLng driverLatLng = new LatLng( latitude , longitude );
                                            String destination = parseUser.getString( "destination" );
                                            String username    = parseUser.getUsername();
                                            mMap.addMarker(new MarkerOptions().position(driverLatLng).
                                                    title( destination ).snippet( username )).showInfoWindow();
                                        }
                                    });

                                }
                            }
                        } else { e.printStackTrace(); }
                    }

                });
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        // getting user permission for GPS.
        if ( ActivityCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
            requestPermissions( new String[] { Manifest.permission.ACCESS_FINE_LOCATION } , 2 );
        else
            locationManager.requestLocationUpdates( locationManager.GPS_PROVIDER , 0 , 0 , locationListener );

    }

    /**
     * gets the markers location on the map and through that,
     * finds the desired user and goes to MakeRequestActivity.
     * @param marker is the marker on the map.
     */
    @Override
    public boolean onMarkerClick( Marker marker )
    {
        // getting the required information of the user
        String username    = marker.getSnippet();
        String destination = marker.getTitle();

        // finding the desired  driver
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo( "username"    , username );
        query.whereEqualTo( "destination" , destination );
        query.whereEqualTo( "driverOrPassenger" , "driver" );
        query.setLimit( 1 );
        query.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> objects, ParseException e)
            {
                if ( e == null  )
                {
                    if ( objects.size() > 0 )
                    {
                        // switching to MakeRequestActivity.
                        Intent intent = new Intent( getApplicationContext(), MakeRequestActivity.class );
                        intent.putExtra("username", objects.get(0).getUsername());
                        startActivity( intent );
                    }

                } else { e.printStackTrace(); }

            }
        });
        return false;
    }

    /**
     * returns to my profile and clears the stack.
     * @param view is the return to MyProfile button.
     */
    public void returnToMyProfile(View view)
    {
        locationManager.removeUpdates( locationListener );
        locationListener = null;

        Intent intent = new Intent( getApplicationContext() , MyProfileActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
    }

    /**
     * creates an option menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.information_menu , menu );
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * enables the actionBarDrawerToggle to pop up.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == R.id.information )
        {
            Intent intent = new Intent( getApplicationContext() , InformationActivity.class );
            startActivity( intent );
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * keeps the user in the current intent.
     */
    @Override
    public void onBackPressed()
    {
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        startActivity( getIntent() );
    }

}