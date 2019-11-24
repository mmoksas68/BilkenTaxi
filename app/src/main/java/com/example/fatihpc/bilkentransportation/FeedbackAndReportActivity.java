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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class FeedbackAndReportActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout     drawerLayout;
    private NavigationView navigationView;
    private EditText      subjectEditText;
    private EditText explanationsEditText;
    private TextView      headingTextView;

    /**
     * declares the properties and sets the heading.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_and_report);

        // declaring properties.
        navigationView        = findViewById( R.id.navigationView  );
        drawerLayout          = findViewById( R.id.drawerLayout    );
        subjectEditText       = findViewById( R.id.subjectEditText );
        explanationsEditText  = findViewById( R.id.commentEditText );
        headingTextView       = findViewById( R.id.headingTextView );
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        // setting TextView
        if (getIntent().getStringExtra("indicator") == null )
        {
            headingTextView.setText("Feedback");
            setTitle("Feedback");
        }
        else if (getIntent().getStringExtra("indicator").equals("report") )
        {
            headingTextView.setText("Report");
            setTitle( "Report" );
        }
    }

    /**
     * sends feedback's and reports to the server.
     * @param view is the send button on the layout.
     */
    public void sendFeedbackOrReport(View view)
    {
        // extracting Strings from the editTexts.
        final String subject      =      subjectEditText.getText().toString();
        final String explanations = explanationsEditText.getText().toString();
        final String userName     =  ParseUser.getCurrentUser().getUsername();

        // sending feedBacks to server.
        ParseObject parseObject = new ParseObject( "Feedbacks" );
        parseObject.put( "userName" , userName );
        parseObject.put( "subject"  , subject  );
        parseObject.put( "explanations" , explanations );
        parseObject.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if ( e == null && !subject.equals("") && !explanations.equals("") )
                {
                    Toast.makeText( getApplicationContext() , "Feedback received, Thank You" , Toast.LENGTH_LONG ).show();
                    Intent intent = new Intent( getApplicationContext() , MyProfileActivity.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity( intent );
                }
                else
                    Toast.makeText( getApplicationContext() , "Feedback couldn't be received, Please try again." , Toast.LENGTH_LONG ).show();
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