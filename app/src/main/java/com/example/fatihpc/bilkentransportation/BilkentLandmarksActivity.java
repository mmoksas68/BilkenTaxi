package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BilkentLandmarksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView      landmarksListView;
    private ArrayAdapter       arrayAdapter;
    private SQLiteDatabase   sqLiteDatabase;
    private ArrayList<String> landMarksName;
    private ArrayList<LatLng>       latLngs;
    private DrawerLayout       drawerLayout;
    private NavigationView   navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilkent_landmarks);
        setTitle( "Bilkent Landmarks" );

        // declaring properties.
        latLngs       = new ArrayList<>();
        landMarksName = new ArrayList<>();
        drawerLayout      = findViewById( R.id.drawerLayout      );
        navigationView    = findViewById( R.id.navigationView    );
        landmarksListView = findViewById( R.id.landmarksListView );
        arrayAdapter = new ArrayAdapter( this , android.R.layout.simple_list_item_1 , landMarksName );
        actionBarDrawerToggle = new ActionBarDrawerToggle( this , drawerLayout , R.string.open , R.string.close );

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        // setting landmarksListView
        landmarksListView.setAdapter( arrayAdapter );
        landmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent( getApplicationContext() , LocationOfLandmarkActivity.class );
                intent.putExtra( "locationName" , landMarksName.get(i)     );
                intent.putExtra( "latitude"     , latLngs.get(i).latitude  );
                intent.putExtra( "longitude"    , latLngs.get(i).longitude );
                startActivity( intent );
            }
        });

        // getting data from the sqLiteDatabase.
        try
        {
            this.deleteDatabase( "LANDMARKS" );
            sqLiteDatabase = this.openOrCreateDatabase( "LANDMARKS" , MODE_PRIVATE , null );
            sqLiteDatabase.execSQL( "CREATE TABLE IF NOT EXISTS LandmarkLocations ( locationName VARCHAR ,"
                    + " latitude DOUBLE , longitude DOUBLE , id INTEGER PRIMARY KEY )" );


            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'SA Building'   , 39.867555 , 32.748561 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'SB Building'   , 39.868306 , 32.748291 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'V Building'    , 39.867277 , 32.750318 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'G Building'    , 39.869139 , 32.749715 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'B Building'    , 39.869219 , 32.749775 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'A Building'    , 39.869196 , 32.750022 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'Library'       , 39.87029 , 32.7496298 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'Health Center' ,  39.868378 , 32.7468633 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'rectorship '   , 39.8712898 , 32.7477701 )" );
            sqLiteDatabase.execSQL( "INSERT INTO LandmarkLocations ( locationName , latitude , longitude ) VALUES ( 'Cyberpark'     , 39.8698151 , 32.7432758 )" );


            // creating a cursor that will read the data from the data base
            Cursor cursor = sqLiteDatabase.rawQuery( "SELECT * FROM LandmarkLocations " , null);
            int locationNameIndex = cursor.getColumnIndex( "locationName" );
            int latitudeIndex     = cursor.getColumnIndex( "latitude"     );
            int longitudeIndex    = cursor.getColumnIndex( "longitude"    );
            int idIndex           = cursor.getColumnIndex( "id"           );
            cursor.moveToFirst();

            // iterating the database.
            while ( cursor != null )
            {
                // getting the desired information's.
                String locationName = cursor.getString( locationNameIndex );
                double latitude     = cursor.getDouble( latitudeIndex     );
                double longitude    = cursor.getDouble( longitudeIndex    );
                int    id           = cursor.getInt( idIndex              );

                // printing out the information's on the console.
                System.out.println( "Location Name : " + locationName );
                System.out.println( "Latitude : " + latitude );
                System.out.println( "Longitude : " + longitude );
                System.out.println( "Location Name : " + id );

                // adding these information's to the related ArrayLists
                latLngs.add( new LatLng( latitude , longitude ) );
                landMarksName.add( locationName );

                // updating listView and moving cursor to next index.
                arrayAdapter.notifyDataSetChanged();
                cursor.moveToNext();
            }

        } catch ( Exception e ) { e.printStackTrace(); }
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
        NavigationBar navigationBar = new NavigationBar();
        return navigationBar.onNavigationItemSelected( item , getApplicationContext() , drawerLayout );
    }

}