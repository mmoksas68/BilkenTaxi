package com.example.fatihpc.bilkentransportation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout           drawerLayout;
    private NavigationView       navigationView;
    private TextView               nameTextView;
    private ImageView          profileImageView;
    private ListView           commentsListView;
    private ArrayAdapter           arrayAdapter;
    private ArrayList<String> commentsArrayList;
    private Switch      driverOrPassengerSwitch;
    private EditText        destinationEditText;
    private LocationManager     locationManager;
    private LocationListener   locationListener;

    /**
     * sets the actionBarDrawerToggle and initialises the properties.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // declaring properties.
        drawerLayout     = findViewById( R.id.drawerLayout     );
        navigationView   = findViewById( R.id.navigationView   );
        nameTextView     = findViewById( R.id.nameTextView     );
        profileImageView = findViewById( R.id.profileImageView );
        commentsListView = findViewById( R.id.commentsListView );
        driverOrPassengerSwitch = findViewById( R.id.driverOrPassengerSwitch );
        destinationEditText     = findViewById( R.id.destinationEditText     );
        commentsArrayList     = new ArrayList<>();
        actionBarDrawerToggle = new ActionBarDrawerToggle( this , drawerLayout , R.string.open , R.string.close );

        // ensuring that the user logs in as a passenger.
        ParseUser.getCurrentUser().put( "driverOrPassenger" , "passenger" );
        ParseUser.getCurrentUser().saveInBackground();

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        // setting profile
        setTitle( "My Profile" );
        setLocationManager();
        setProfile();
    }

    /**
     * sets the location manager.
     * sets the location listener.
     * gets user permission for GPS.
     */
    public void setLocationManager()
    {
        locationManager = (LocationManager) this.getSystemService( LOCATION_SERVICE );
        locationListener = new LocationListener()
        {
            /**
             * sets the location to the server and
             * trying to find passengers for drivers.
             * @param location is the updated location.
             */
            @Override
            public void onLocationChanged(Location location)
            {
                if ( ParseUser.getCurrentUser() == null )
                {
                    locationManager.removeUpdates( locationListener );
                    locationListener = null;
                }

                else if ( ParseUser.getCurrentUser() != null )
                {
                    // setting the destinationEditText.
                    String destination = destinationEditText.getText().toString();
                    ParseUser.getCurrentUser().put("destination", destination);

                    // updating current users location.
                    double latitude  = location.getLatitude();
                    double longitude = location.getLongitude();
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latitude, longitude);
                    ParseUser.getCurrentUser().put("location", parseGeoPoint);
                    ParseUser.getCurrentUser().saveInBackground();

                    // if the current user is driver, then searching for requests.
                    if ( ParseUser.getCurrentUser().getString("driverOrPassenger").equals("driver") )
                    {
                        // trying to find a new Request.
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Requests");
                        String userId = ParseUser.getCurrentUser().getObjectId();
                        parseQuery.whereEqualTo( "requestedDriversUserId" , userId );
                        parseQuery.whereDoesNotExist( "driversLocation" );
                        parseQuery.whereDoesNotExist( "isAccepted" );
                        List<String> blockedUsers = ParseUser.getCurrentUser().getList("blockedUsers");
                        parseQuery.whereNotContainedIn( "passengersUserId" , blockedUsers );
                        parseQuery.setLimit(1);
                        parseQuery.findInBackground(new FindCallback<ParseObject>()
                        {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e)
                            {
                                if (e == null && objects.size() > 0)
                                {
                                    // getting to NewRequestActivity and passing Request id.
                                    Intent intent = new Intent(getApplicationContext(), NewRequestActivity.class );
                                    intent.putExtra( "passengersUserId", objects.get(0).getString("passengersUserId") );
                                    intent.putExtra( "passengerRequestId", objects.get(0).getObjectId() );
                                    startActivity(intent);

                                    // removing the updates
                                    locationManager.removeUpdates(locationListener);
                                    locationListener = null;
                                }
                            }
                        });
                    }
                }
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        // getting user permission for GPS and requesting location updates.
        if ( ActivityCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            requestPermissions( new String[] { Manifest.permission.ACCESS_FINE_LOCATION } , 2 );
        else
            locationManager.requestLocationUpdates( locationManager.GPS_PROVIDER , 0 , 0 , locationListener );
    }

    /**
     * determines what to do after getting permissions.
     * @param requestCode is the permission request code.
     * @param permissions is the permission type.
     * @param grantResults is the result of
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // getting user permission for GPS and requesting location updates.
        if ( requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED )
            locationManager.requestLocationUpdates( locationManager.GPS_PROVIDER , 0 , 0 , locationListener );
    }

    /**
     * sets the profile of the selected user.
     */
    public void setProfile()
    {
        // setting the destinationEditText.
        String destination = ParseUser.getCurrentUser().getString( "destination" );
        destinationEditText.setText( destination );

        // setting commentsListView
        arrayAdapter = new ArrayAdapter( getApplicationContext() , android.R.layout.simple_list_item_1 , commentsArrayList );
        commentsListView.setAdapter( arrayAdapter );

        // setting name Text View
        nameTextView.setText( ParseUser.getCurrentUser().getUsername() );

        // setting profile ImageView
        if ( ParseUser.getCurrentUser().getParseFile("image" ) != null )
        {
            ParseFile parseFile = ParseUser.getCurrentUser().getParseFile("image" );
            parseFile.getDataInBackground(new GetDataCallback()
            {
                @Override
                public void done( byte[] data , ParseException e )
                {
                    if ( e == null && data != null )
                    {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length );
                        profileImageView.setImageBitmap(bitmap);
                    }
                }
            });
        }

        // constructing commentsListView.
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Comments");
        String userName = ParseUser.getCurrentUser().getUsername();
        parseQuery.whereEqualTo( "commentReceiver" , userName );
        parseQuery.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if ( objects != null && e == null )
                {
                    for ( ParseObject parseObject : objects )
                    {
                        // constructing comment
                        String comment       = parseObject.getString( "comment" );
                        String commentMaker  = parseObject.getString( "commentMaker" );
                        String commentResult = comment + " (" + commentMaker + ")";

                        // adding comment to arrayList.
                        commentsArrayList.add( commentResult );
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * determines whether the user is a driver or passenger.
     * @param view is the switch button that arranges the state.
     */
    public void switchToDriverOrPassenger(View view)
    {
        // switching to driver.
        if ( driverOrPassengerSwitch.isChecked() )
        {
            ParseUser.getCurrentUser().put( "driverOrPassenger" , "driver" );
            ParseUser.getCurrentUser().saveInBackground();
            Toast.makeText( getApplicationContext() , "Switched to driver" , Toast.LENGTH_LONG ).show();
        }
        // switching to passenger.
        else if ( !driverOrPassengerSwitch.isChecked() )
        {
            ParseUser.getCurrentUser().put( "driverOrPassenger", "passenger" );
            ParseUser.getCurrentUser().saveInBackground();
            Toast.makeText( getApplicationContext() , "Switched to passenger" , Toast.LENGTH_LONG ).show();
        }
    }

    /**
     * starts the EditProfileActivity to edit profile.
     * @param view is the edit profile button.
     */
    public void editProfile(View view)
    {
        Intent intent = new Intent( getApplicationContext() , EditProfileActivity.class );
        startActivity( intent );
    }

    /**
     * keeps the user in the current intent.
     */
    @Override
    public void onBackPressed()
    {
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
        if ( actionBarDrawerToggle.onOptionsItemSelected(item) )
            return true;

        else if ( item.getItemId() == R.id.information )
        {
            Intent intent = new Intent( getApplicationContext() , InformationActivity.class );
            startActivity( intent );
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * adds each item functionality in the navigationView.
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item )
    {
        locationManager.removeUpdates( locationListener );
        locationListener = null;

        NavigationBar navigationBar = new NavigationBar();
        return navigationBar.onNavigationItemSelected( item , getApplicationContext() , drawerLayout );
    }

}