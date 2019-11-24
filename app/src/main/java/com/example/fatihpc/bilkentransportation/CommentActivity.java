package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CommentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout     drawerLayout;
    private NavigationView navigationView;
    private EditText      commentEditText;
    private String      usernameOfProfile;

    /**
     * sets the actionBarDrawerToggle and initialises the properties.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // declaring properties.
        drawerLayout    = findViewById( R.id.drawerLayout    );
        navigationView  = findViewById( R.id.navigationView  );
        commentEditText = findViewById( R.id.commentEditText );
        actionBarDrawerToggle = new ActionBarDrawerToggle( this , drawerLayout , R.string.open , R.string.close );
        usernameOfProfile = getIntent().getStringExtra( "usernameOfProfile" );

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * adds a comment for a user.
     * @param view is the add comment button.
     */
    public void addComment(View view)
    {
        // extracting the required strings to store data on server.
        String comment         = commentEditText.getText().toString();
        String commentMaker    = ParseUser.getCurrentUser().getUsername();
        String commentReceiver = usernameOfProfile;

        // putting data to server.
        ParseObject parseObject = new ParseObject("Comments");
        parseObject.put( "comment" , comment );
        parseObject.put( "commentMaker" , commentMaker );
        parseObject.put( "commentReceiver" , commentReceiver );
        parseObject.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if ( e == null )
                {
                    Toast.makeText(getApplicationContext(), "comment added", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText( getApplicationContext() , e.getLocalizedMessage() , Toast.LENGTH_LONG ).show();
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