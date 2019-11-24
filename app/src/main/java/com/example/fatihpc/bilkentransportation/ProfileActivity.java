package com.example.fatihpc.bilkentransportation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout     drawerLayout;
    private NavigationView navigationView;
    private TextView         nameTextView;
    private ImageView    profileImageView;
    private ListView     commentsListView;
    private ArrayAdapter     arrayAdapter;
    private ArrayList<String>    comments;
    private String      usernameOfProfile;
    private String            idOfProfile;

    /**
     * sets the actionBarDrawerToggle.
     * initialises the properties.
     * sets the profile of the selected user.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // declaring properties.
        comments         = new ArrayList<>();
        drawerLayout     = findViewById( R.id.drawerLayout     );
        navigationView   = findViewById( R.id.navigationView   );
        nameTextView     = findViewById( R.id.nameTextView     );
        profileImageView = findViewById( R.id.profileImageView );
        commentsListView = findViewById( R.id.commentsListView );
        actionBarDrawerToggle = new ActionBarDrawerToggle( this , drawerLayout , R.string.open , R.string.close );

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        // sets the profile
        setTitle( "Profile" );
        setProfile();
    }

    /**
     * sets the profile of the selected user.
     */
    public void setProfile()
    {
        // setting commentsListView
        arrayAdapter = new ArrayAdapter( getApplicationContext() , android.R.layout.simple_list_item_1 , comments );
        commentsListView.setAdapter( arrayAdapter );

        // getting the user name of the indicated user.
        usernameOfProfile = getIntent().getStringExtra( "username" );

        // setting the components according to the indicated user.
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo( "username" , usernameOfProfile );
        parseQuery.setLimit(1);
        parseQuery.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> objects, ParseException e)
            {
                if ( e == null && objects.size() > 0 )
                {
                    ParseUser parseUser= objects.get(0);
                    idOfProfile = parseUser.getObjectId();

                    // setting nameTextView
                    nameTextView.setText( parseUser.getUsername() );

                    // setting profile ImageView
                    if ( parseUser.getParseFile("image") != null )
                    {
                        ParseFile parseFile = parseUser.getParseFile("image");
                        parseFile.getDataInBackground(new GetDataCallback()
                        {
                            @Override
                            public void done(byte[] data, ParseException e)
                            {
                                if (e == null && data != null)
                                {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profileImageView.setImageBitmap(bitmap);

                                } else { System.out.println( e.getLocalizedMessage() ); }
                            }
                        });
                    }
                    else
                    {
                        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.user_profile);
                        profileImageView.setImageBitmap(bitmap);
                    }

                }
            }
        });

        // constructing commentsListView.
        ParseQuery<ParseObject> parseQuery1 = new ParseQuery<ParseObject>("Comments");
        parseQuery1.whereEqualTo( "commentReceiver" , usernameOfProfile );
        parseQuery1.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                for ( ParseObject parseObject : objects )
                {
                    String comment = parseObject.getString( "comment" ) + " ("
                            + parseObject.getString( "commentMaker" ) + ")";

                    comments.add( comment );
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * gets to the previous activity.
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent( getApplicationContext() , SearchProfileActivity.class );
        startActivity( intent ); finish();
    }

    /**
     * Refreshes the screen.
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        startActivity( getIntent() );
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
     * enables to make a comment for an other user.
     * @param view is the comment button on the layout.
     */
    public void makeComment(View view)
    {
        Intent intent = new Intent( getApplicationContext() , CommentActivity.class );
        intent.putExtra( "usernameOfProfile" , usernameOfProfile );
        startActivity( intent );
    }

    /**
     * enables to report another user.
     * @param view is the report button.
     */
    public void report(View view)
    {
        Intent intent = new Intent( getApplicationContext() , FeedbackAndReportActivity.class );
        intent.putExtra( "indicator" , "report" );
        startActivity( intent );
    }

    /**
     * enables a user to block somebody.
     * @param view is the block button.
     */
    public void block(View view)
    {
        // asking for to be sure
        new AlertDialog.Builder(this).setTitle("Are you sure that you want to block the user.").
                setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // blocking user.
                        ParseUser.getCurrentUser().add( "blockedUsers" , idOfProfile );
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback()
                        {
                            @Override
                            public void done(ParseException e)
                            {
                                if ( e == null )
                                {
                                    // going back to SearchProfileActivity.
                                    Toast.makeText( getApplicationContext() , "User Blocked" , Toast.LENGTH_LONG ).show();
                                    Intent intent = new Intent( getApplicationContext() , SearchProfileActivity.class );
                                    startActivity( intent ); finish();
                                }
                            }
                        });
                    }
                }).setNegativeButton( "No" , null ) .show();
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