package com.example.fatihpc.bilkentransportation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ConfirmationActivity extends AppCompatActivity
{
    // constants
    private static final int REQUEST_CALL = 1;

    // properties
    private TextView phoneNumberTextView;
    private String phoneNumber;
    private String    driverId;

    /**
     * sets the actionBarDrawerToggle and initialises the properties.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        getSupportActionBar().hide();

        // declaring properties
        phoneNumberTextView = findViewById( R.id.phoneNumberTextView );
        driverId  =  getIntent().getStringExtra(  "driverId"    );

        // setting phone number of the driver.
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.getInBackground( driverId , new GetCallback<ParseUser>()
        {
            @Override
            public void done(ParseUser object, ParseException e)
            {
                if ( e == null && object != null )
                {
                    phoneNumber = object.getString("phoneNumber");
                    String text = "Phone Number : " + phoneNumber;
                    phoneNumberTextView.setText( text );
                }
            }
        });

    }

    /**
     * enables to make a call to the appeared number.
     * @param view is the call button on the layout.
     */
    public void call(View view)
    {
        String number = phoneNumber;

        // If permission is not given, get permission first. If permission is given make the phone call.
        if (ContextCompat.checkSelfPermission(ConfirmationActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ConfirmationActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        else
        {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

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

        String number = phoneNumber;

        // getting permission for phone call.
        if (requestCode == REQUEST_CALL)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ConfirmationActivity.this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
            else { Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show(); }
        }
    }

    /**
     * enables to return to MyProfileActivity.
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();

        Intent intent = new Intent( getApplicationContext() , MyProfileActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        finish();
    }

}