package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignInActivity extends AppCompatActivity
{
    // properties
    private EditText userNameEditText;
    private EditText passwordEditText;

    /**
     * initialises the properties of the class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseUser.getCurrentUser().logOut();
        getSupportActionBar().hide();

        // declaring properties
        userNameEditText = findViewById( R.id.userNameEditText );
        passwordEditText = findViewById( R.id.passwordEditText );
    }

    /**
     * starts the SignUpActivity.
     * @param view is the TextView.
     */
    public void tapToSignUp(View view)
    {
        Intent intent = new Intent( getApplicationContext() , SignUpActivity.class );
        startActivity( intent );
    }

    /**
     * enables a user to signing in.
     * @param view is the signIn button.
     */
    public void signIn(View view)
    {
        // getting the texts from editTexts.
        String userName = userNameEditText.getText().toString();
        String passWord = passwordEditText.getText().toString();

        // Logging in.
        ParseUser.logInInBackground(userName, passWord, new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException e)
            {
                if ( e == null && user != null )
                {
                    Toast.makeText(getApplicationContext(), "Hello " + user.getUsername(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent( getApplicationContext() , MyProfileActivity.class );
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity( intent );
                }
                else
                    Toast.makeText( getApplicationContext() , e.getLocalizedMessage() , Toast.LENGTH_LONG ).show();
            }
        });
    }

}