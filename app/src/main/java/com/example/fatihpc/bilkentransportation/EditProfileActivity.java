package com.example.fatihpc.bilkentransportation;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    // properties
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout      drawerLayout;
    private NavigationView  navigationView;
    private EditText      userNameEditText;
    private EditText      passwordEditText;
    private EditText passwordCheckEditText;
    private EditText   phoneNumberEditText;
    private EditText         emailEditText;
    private ImageView     profileImageView;
    private Bitmap             chosenImage;

    /**
     * sets the actionBarDrawerToggle and initialises the properties.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // declaring properties.
        drawerLayout          = findViewById( R.id.drawerLayout          );
        navigationView        = findViewById(R.id.navigationView         );
        userNameEditText      = findViewById( R.id.userNameEditText      );
        passwordEditText      = findViewById( R.id.passwordEditText      );
        passwordCheckEditText = findViewById( R.id.passwordCheckEditText );
        phoneNumberEditText   = findViewById( R.id.phoneNumberEditText   );
        emailEditText         = findViewById( R.id.emailEditText         );
        profileImageView      = findViewById( R.id.profileImageView      );
        actionBarDrawerToggle = new ActionBarDrawerToggle( this , drawerLayout , R.string.open , R.string.close );

        // setting actionBarDrawerToggle.
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting OnNavigationItemSelectedListener to navigationView.
        navigationView.setNavigationItemSelectedListener(this);

        // sets profile
        setTitle( "Edit Profile" );
        setProfile();
    }

    /**
     * sets the profile of the current user.
     */
    public void setProfile()
    {
        // getting required information
        String email       = ParseUser.getCurrentUser().getEmail();
        String userName    = ParseUser.getCurrentUser().getUsername();
        String password    = ParseUser.getCurrentUser().getString( "password" );
        String phoneNumber = ParseUser.getCurrentUser().getString( "phoneNumber" );

        // setting Edit Texts
        emailEditText.setText( email );
        userNameEditText.setText( userName );
        phoneNumberEditText.setText( phoneNumber );

        // setting profile ImageView
        if ( ParseUser.getCurrentUser().getParseFile("image") != null )
        {
            ParseFile parseFile = ParseUser.getCurrentUser().getParseFile("image" );
            parseFile.getDataInBackground(new GetDataCallback()
            {
                @Override
                public void done(byte[] data, ParseException e)
                {
                    if (e == null && data != null)
                    {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profileImageView.setImageBitmap(bitmap);

                    } else { e.printStackTrace(); }
                }
            });
        }
    }

    /**
     * saves the changed profile to the server.
     * @param view the save button on the layout.
     */
    public void saveProfile(View view)
    {
        // getting required information's.
        String userName      = userNameEditText.getText().toString();
        String phoneNumber   = phoneNumberEditText.getText().toString();
        String email         = emailEditText.getText().toString();
        String password      = passwordEditText.getText().toString();
        String checkPassword = passwordCheckEditText.getText().toString();

        // checking whether passwords match.
        if ( password.equals( checkPassword ) )
        {
            if ( password.length() >= 6  )
            {

                // if image is changed, converting it to parse format.
                if (chosenImage != null)
                {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    chosenImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArrayUserImage = byteArrayOutputStream.toByteArray();
                    ParseFile parseFile = new ParseFile("image.png", byteArrayUserImage);
                    ParseUser.getCurrentUser().put("image", parseFile);
                }

                // setting the gathered information's.
                ParseUser.getCurrentUser().setUsername(userName);
                ParseUser.getCurrentUser().put("phoneNumber", phoneNumber);
                ParseUser.getCurrentUser().setEmail(email);
                ParseUser.getCurrentUser().setPassword(password);

                // saving the information's to server.
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(ParseException e)
                    {
                        if (e == null)
                        {
                            Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else { Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show(); }
                    }
                });
            }
            else
                Toast.makeText(getApplicationContext(), "Password should be more than 6 letters.", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText( getApplicationContext() , "Password does not match." , Toast.LENGTH_LONG ).show();
    }

    /**
     * takes the user to previous activity.
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    /**
     * changes the profile image of the user.
     * @param view is the user profile image.
     */
    public void tapToChangeProfileImage(View view)
    {
        // getting permission from the user in order to data access.
        if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
            requestPermissions( new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE } , 2 );

        // Having permission, directing the user to gallery.
        else
        {
            Intent intent = new Intent( Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( intent , 2 );
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
        // Having permission, directing the user to gallery.
        if ( requestCode == 2 && grantResults.length > 0
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED  )
        {
            Intent intent = new Intent( Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( intent , 2 );
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * gets the image and sets it as the profile.
     * @param requestCode is the request code.
     * @param resultCode is the result code.
     * @param data is the data for the profile image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == 2 && resultCode == RESULT_OK && data != null )
        {
            try
            {
                ContentResolver resolver = getApplicationContext().getContentResolver();
                chosenImage = MediaStore.Images.Media.getBitmap( resolver , data.getData() );
                profileImageView.setImageBitmap( chosenImage );

            } catch (IOException e) { e.printStackTrace(); }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
