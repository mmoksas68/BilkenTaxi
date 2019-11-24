package com.example.fatihpc.bilkentransportation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class NewRequestActivity extends AppCompatActivity
{
    // properties
    private TextView        nameTextView;
    private ImageView   profileImageView;
    private ListView    commentsListView;
    private ArrayAdapter    arrayAdapter;
    private ArrayList<String>   comments;
    private String      passengersUserId;
    private String     usernameOfProfile;
    private String    passengerRequestId;
    private CountDownTimer countDownTimer;

    /**
     * sets the actionBarDrawerToggle.
     * initialises the properties.
     * sets the profile of the selected user.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        // declaring properties.
        passengerRequestId = getIntent().getStringExtra( "passengerRequestId" );
        nameTextView     = findViewById( R.id.nameTextView     );
        profileImageView = findViewById( R.id.profileImageView );
        commentsListView = findViewById( R.id.commentsListView );
        comments = new ArrayList<>();

        // setting countDownTimer for not handling.
        setCountDownTimer();

        // sets the profile
        setProfile();
    }

    /**
     * sets the countDownTimer in case of not handling request.
     */
    public void setCountDownTimer()
    {
        countDownTimer = new CountDownTimer( 14000  , 1000 )
        {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish()
            {
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery( "Requests" );
                parseQuery.getInBackground( passengerRequestId , new GetCallback<ParseObject>()
                {
                    @Override
                    public void done(ParseObject object, ParseException e)
                    {
                        if ( e == null )
                        {
                            if ( object != null )
                            {
                                if (!object.containsKey("isAccepted"))
                                {
                                    try
                                    {
                                        object.delete();

                                    } catch (ParseException e1) { e1.printStackTrace(); }

                                    Intent intent = new Intent( getApplicationContext() , MyProfileActivity.class );
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity( intent );
                                }
                            }
                        }

                        else
                        {
                            Intent intent = new Intent( getApplicationContext() , MyProfileActivity.class );
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity( intent );
                        }

                    }
                });
            }
        }.start();
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
        passengersUserId = getIntent().getStringExtra( "passengersUserId" );

        // setting the components according to the indicated user.
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.getInBackground( passengersUserId , new GetCallback<ParseUser>()
        {
            @Override
            public void done(ParseUser object, ParseException e)
            {
                if ( e == null && object != null )
                {
                    // setting nameTextView
                    usernameOfProfile = object.getUsername();
                    nameTextView.setText( usernameOfProfile);

                    // setting profile ImageView
                    if ( object.getParseFile("image") != null )
                    {
                        ParseFile parseFile = object.getParseFile("image");
                        parseFile.getDataInBackground(new GetDataCallback()
                        {
                            @Override
                            public void done(byte[] data, ParseException e)
                            {
                                if (e == null && data != null)
                                {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length );
                                    profileImageView.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }

                    else // in case of not having a user profile image, setting default.
                    {
                        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.user_profile);
                        profileImageView.setImageBitmap(bitmap);
                    }

                    // constructing commentsListView.
                    ParseQuery<ParseObject> parseQuery1 = new ParseQuery<>("Comments");
                    parseQuery1.whereEqualTo( "commentReceiver" , usernameOfProfile );
                    parseQuery1.findInBackground(new FindCallback<ParseObject>()
                    {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e)
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
                    });

                }

            }

        });

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
     * accepts the request.
     * @param view is the accept button.
     */
    public void accept(View view)
    {
        String passengerRequestId = getIntent().getStringExtra( "passengerRequestId" );
        final Context context = this;

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery( "Requests" );
        parseQuery.getInBackground(passengerRequestId, new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject object, ParseException e)
            {
                if ( e == null && object != null )
                {
                    object.put( "isAccepted" , true );
                    object.put( "driversLocation" , ParseUser.getCurrentUser().getParseGeoPoint( "location" ) );
                    object.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if ( e == null )
                            {
                                countDownTimer.cancel();
                                new AlertDialog.Builder(context).setTitle("Please wait for passengers phone call").
                                        setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }) .show();
                            }
                            else
                                Toast.makeText( getApplicationContext() , e.getLocalizedMessage() , Toast.LENGTH_LONG ).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * denies the request.
     * @param view is the deny button.
     */
    public void deny(View view)
    {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery( "Requests" );
        parseQuery.getInBackground(passengerRequestId, new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject object, ParseException e)
            {
                if ( e == null && object != null )
                {
                    object.put("isAccepted", false);
                    object.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if (e == null)
                            {
                                countDownTimer.cancel();
                                Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
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
                        // finding the passenger in database.
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery( "Requests" );
                        parseQuery.getInBackground(passengerRequestId, new GetCallback<ParseObject>()
                        {
                            @Override
                            public void done(ParseObject object, ParseException e)
                            {
                                if ( e == null && object != null )
                                {
                                    // denying the request.
                                    object.put("isAccepted", false);
                                    object.saveInBackground(new SaveCallback()
                                    {
                                        @Override
                                        public void done(ParseException e)
                                        {
                                            if (e == null)
                                            {
                                                // blocking user.
                                                ParseUser.getCurrentUser().add( "blockedUsers", passengersUserId );
                                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback()
                                                {
                                                    @Override
                                                    public void done(ParseException e)
                                                    {
                                                        if ( e == null )
                                                        {
                                                            countDownTimer.cancel();
                                                            Toast.makeText( getApplicationContext() , "User Blocked" , Toast.LENGTH_LONG ).show();
                                                            Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).setNegativeButton( "No" , null ) .show();
    }

}