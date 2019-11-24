package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<String>     profileNamesArrayList;
    private DrawerLayout     drawerLayout;
    private android.support.v7.widget.SearchView searchView;
    private NavigationView navigationView;
    private ListView     profilesListView;
    private ArrayAdapter     arrayAdapter;

    /**
     * sets the actionBarDrawerToggle
     * initialises the properties.
     * calls the setListView() method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);

        // declaring properties.
        drawerLayout     = findViewById( R.id.drawerLayout     );
        navigationView   = findViewById( R.id.navigationView   );
        profilesListView = findViewById( R.id.theList );
        profileNamesArrayList = new ArrayList<>();
        actionBarDrawerToggle = new ActionBarDrawerToggle( this , drawerLayout , R.string.open , R.string.close );

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        // setting listView;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search Profiles");

        setListView();
    }

    /**
     * sets the profilesListView.
     */
    private void setListView()
    {
        // setting listView
        arrayAdapter = new ArrayAdapter( getApplicationContext() , android.R.layout.simple_list_item_1 , profileNamesArrayList );
        profilesListView.setAdapter(arrayAdapter);
        profilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent( getApplicationContext() , ProfileActivity.class );
                intent.putExtra( "username" , profileNamesArrayList.get( i ) );
                startActivity( intent ); finish();
            }
        });

        // constructing profileNamesArrayList.
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo( "username" , ParseUser.getCurrentUser().getUsername() );
        List<String> blockedUsers = ParseUser.getCurrentUser().getList("blockedUsers");
        parseQuery.whereNotContainedIn( "objectId" , blockedUsers );
        parseQuery.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> objects, ParseException e)
            {
                if ( e == null )
                {
                    for (ParseUser parseUser : objects)
                    {
                        String profileName = parseUser.getUsername();
                        profileNamesArrayList.add( profileName );
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
                else { System.out.println( e.getLocalizedMessage() ); }
            }
        });

    }

    /**
     * creates an option menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.profile_menu , menu );
        MenuItem mSearch = menu.findItem(R.id.action_search);
        searchView = (android.support.v7.widget.SearchView) mSearch.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });
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