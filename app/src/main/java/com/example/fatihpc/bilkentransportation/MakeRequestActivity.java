package com.example.fatihpc.bilkentransportation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class MakeRequestActivity extends AppCompatActivity
{
    // properties
    private TextView destinationTextView;
    private TextView        nameTextView;
    private ImageView   profileImageView;
    private ListView    commentsListView;
    private ArrayAdapter    arrayAdapter;
    private ArrayList<String>   comments;
    private String     usernameOfProfile;
    private String           idOfProfile;

    /**
     * declaring properties and setting profile.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);

        // declaring properties.
        destinationTextView = findViewById( R.id.destinationTextView );
        nameTextView        = findViewById( R.id.nameTextView        );
        profileImageView    = findViewById( R.id.profileImageView    );
        commentsListView    = findViewById( R.id.commentsListView    );
        comments = new ArrayList<>(); setProfile();
    }

    /**
     * sets the profile of the driver.
     */
    public void setProfile()
    {
        // setting commentsListView
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, comments);
        commentsListView.setAdapter(arrayAdapter);

        // getting the user name of the indicated user.
        usernameOfProfile = getIntent().getStringExtra( "username" );

        // setting the components according to the indicated user.
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo( "username" , usernameOfProfile );
        parseQuery.setLimit( 1 );
        parseQuery.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> objects, ParseException e)
            {
                if ( e == null )
                {
                    if ( objects.size() > 0 )
                    {
                        // getting the id of the driver
                        idOfProfile = objects.get(0).getObjectId();

                        // setting nameTextView and destinationTextView
                        String driverName  = objects.get(0).getUsername();
                        String destination = objects.get(0).getString( "destination" );
                        destinationTextView.setText( destination );
                        nameTextView.setText( driverName );

                        // setting profile ImageView
                        if ( objects.get(0).getParseFile("image") != null )
                        {
                            ParseFile parseFile = objects.get(0).getParseFile( "image" );
                            parseFile.getDataInBackground( new GetDataCallback()
                            {
                                @Override
                                public void done(byte[] data, ParseException e)
                                {
                                    if (e == null && data != null)
                                    {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        profileImageView.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        }

                        else // not having an image in server, setting default profile image
                        {
                            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.user_profile);
                            profileImageView.setImageBitmap(bitmap);
                        }

                        // constructing commentsListView.
                        ParseQuery<ParseObject> query = new ParseQuery<>("Comments" );
                        query.whereEqualTo( "commentReceiver" , usernameOfProfile );
                        query.findInBackground( new FindCallback<ParseObject>()
                        {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e)
                            {
                                if ( e == null && objects.size() > 0 )
                                {
                                    for ( ParseObject parseObject : objects )
                                    {
                                        // constructing comment
                                        String comment       = parseObject.getString( "comment" );
                                        String commentMaker  = parseObject.getString( "commentMaker" );
                                        String commentResult = comment + " (" + commentMaker + ")";

                                        // adding comment to arrayList.
                                        comments.add( commentResult );
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                } else { e.printStackTrace(); }

            }
        });
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

    @Override
    public void onBackPressed()
    {
        finish();
    }

    /**
     * enables to make comment about the indicated driver.
     * @param view is the make comment button on the layout.
     */
    public void makeComment(View view)
    {
        Intent intent = new Intent( getApplicationContext() , CommentActivity.class );
        intent.putExtra( "usernameOfProfile" , usernameOfProfile );
        startActivity( intent );
    }

    /**
     * enables to report the indicated driver.
     * @param view is the report button.
     */
    public void report(View view)
    {
        Intent intent = new Intent( getApplicationContext() , FeedbackAndReportActivity.class );
        intent.putExtra( "indicator" , "report" );
        startActivity( intent );
    }

    /**
     * enables to make request to the indicated driver.
     * @param view is the make request button.
     */
    public void makeRequest(View view)
    {
        // makes a request and saves it to database.
        final ParseObject parseObject = new ParseObject( "Requests" );
        parseObject.put( "passenger" , ParseUser.getCurrentUser().getUsername() );
        parseObject.put( "requestedDriver" , usernameOfProfile );
        parseObject.put( "requestedDriversUserId" , idOfProfile );
        parseObject.put( "passengersUserId" , ParseUser.getCurrentUser().getObjectId() );
        parseObject.saveInBackground( new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if ( e == null )
                {
                    Toast.makeText( getApplicationContext() , "Request Made" , Toast.LENGTH_LONG ).show();

                    Intent intent = new Intent( getApplicationContext() , WaitingForResponseActivity.class );
                    intent.putExtra( "passengerRequestId" , parseObject.getObjectId() );
                    startActivity( intent );
                    finish();
                }
                else
                    Toast.makeText( getApplicationContext() , e.getLocalizedMessage() , Toast.LENGTH_LONG ).show();
            }
        });
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
                        // blocking the user.
                        ParseUser.getCurrentUser().add( "blockedUsers" , idOfProfile );
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback()
                        {
                            @Override
                            public void done(ParseException e)
                            {
                                if ( e == null )
                                    finish();
                            }
                        });
                    }
                }).setNegativeButton( "No" , null ) .show();
    }

}