package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class WaitingForResponseActivity extends AppCompatActivity
{
    // properties
    String requestId;
    String  driverId;
    CountDownTimer countDownTimer;
    ImageView waitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_response);

        waitImageView = findViewById( R.id.waitImageView );
        waitImageView.animate().rotation( 1500 ).setDuration( 15000 );

        // setting the requestId and driverId.
        requestId = getIntent().getStringExtra( "passengerRequestId" );
        driverId  = getIntent().getStringExtra( "driverId" );

        // starting timer.
        countDownTimer = new CountDownTimer( 15000 , 1000 )
        {
            /**
             * in every tick, checking whether the request is accepted or denied.
             * @param l is the remaining time for finish.
             */
            @Override
            public void onTick(long l)
            {
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery( "Requests" );
                parseQuery.getInBackground( requestId , new GetCallback<ParseObject>()
                {
                    @Override
                    public void done( ParseObject object, ParseException e )
                    {
                        // being accepted
                        if ( object.containsKey( "isAccepted" ) && object.getBoolean( "isAccepted" ) )
                        {
                            Intent intent = new Intent( getApplicationContext() , ConfirmationActivity.class );
                            intent.putExtra( "driverId" , object.getString( "requestedDriversUserId" ) );

                            try
                            {
                                object.delete();
                            }
                            catch (ParseException e1)  { e1.printStackTrace(); }

                            startActivity( intent );
                            countDownTimer.cancel();
                            finish();
                        }

                        // being denied
                        else if ( object.containsKey( "isAccepted" ) && !object.getBoolean( "isAccepted" ) )
                        {
                            try
                            {
                                object.delete();
                            }
                            catch (ParseException e1)  { e1.printStackTrace(); }

                            Intent intent = new Intent( getApplicationContext() , DenyActivity.class );
                            startActivity( intent );
                            countDownTimer.cancel();
                            finish();
                        }
                    }
                });
            }

            /**
             * if the driver doesn't response to the request.
             */
            @Override
            public void onFinish()
            {
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery( "Requests" );
                parseQuery.getInBackground( requestId , new GetCallback<ParseObject>()
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
                                    }
                                    catch (ParseException e1) { e1.printStackTrace(); }

                                    Intent intent = new Intent(getApplicationContext(), DenyActivity.class);
                                    startActivity(intent);
                                    countDownTimer.cancel();
                                    finish();
                                }
                            }
                        }

                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), DenyActivity.class);
                            startActivity(intent);
                            countDownTimer.cancel();
                            finish();
                        }

                    }
                });
            }
        }.start();

    }

}