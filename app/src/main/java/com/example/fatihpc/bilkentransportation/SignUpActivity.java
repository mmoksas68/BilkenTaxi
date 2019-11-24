package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity
{
    // properties
    private EditText         emailEditText;
    private EditText      userNameEditText;
    private EditText   phoneNumberEditText;
    private EditText      passwordEditText;
    private EditText checkPasswordEditText;

    /**
     * initialises the properties of the class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        setTitle( "Sign Up" );

        // declaring variables
        emailEditText         = findViewById( R.id.eMailEditText         );
        userNameEditText      = findViewById( R.id.userNameEditText      );
        phoneNumberEditText   = findViewById( R.id.phoneNumberEditText   );
        passwordEditText      = findViewById( R.id.passwordEditText      );
        checkPasswordEditText = findViewById( R.id.checkPasswordEditText );
    }

    /**
     * enables a user to sign up.
     * @param view is the sign up button.
     */
    public void signUp(View view)
    {
        // extracting texts from edit texts.
        String email         =         emailEditText.getText().toString();
        String userName      =      userNameEditText.getText().toString();
        String phoneNumber   =   phoneNumberEditText.getText().toString();
        String password      =      passwordEditText.getText().toString();
        String passwordCheck = checkPasswordEditText.getText().toString();

        // checking the extracted texts.
        if ( password.equals( passwordCheck ) )
        {
            if ( password.length() >= 6 )
            {
                if ( phoneNumber.length() >= 11  )
                {
                    if ( userName.toString().length() > 0 )
                    {
                        //if ( email.contains( "bilkent.edu.tr" ) )
                        //{
                            // sending information to server and signing up.
                            ParseUser parseUser = new ParseUser();
                            parseUser.setEmail(email);
                            parseUser.setUsername(userName);
                            parseUser.addAll( "blockedUsers" , new ArrayList<String>());
                            parseUser.put( "driverOrPassenger" , "passenger" );
                            parseUser.put( "phoneNumber" , phoneNumber );
                            parseUser.setPassword(password);
                            parseUser.signUpInBackground(new SignUpCallback()
                            {
                                @Override
                                public void done(ParseException e)
                                {
                                    if (e == null)
                                    {
                                        Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else
                                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        //} else
                          //  Toast.makeText(getApplicationContext(), "Please type bilkent mail.", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Please type user name.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "Phone number should be entered.", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "Password should be more than 6 letters.", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Passwords doesn't match with each other.", Toast.LENGTH_LONG).show();
    }

}