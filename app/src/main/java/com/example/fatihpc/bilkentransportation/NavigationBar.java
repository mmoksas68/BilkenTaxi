package com.example.fatihpc.bilkentransportation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NavigationBar
{

    /**
     * adds each item functionality in the navigationView.
     */
    public boolean onNavigationItemSelected(MenuItem item , final Context context , DrawerLayout drawerLayout )
    {
        // My Profile
        if ( item.getItemId() == R.id.nav_my_profile )
        {
            Intent intent = new Intent( context , MyProfileActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            context.startActivity( intent );
        }

        // Show Vehicle
        else if ( item.getItemId() == R.id.nav_show_vehicle )
        {
            // allowing only to enter passengers.
            if ( ParseUser.getCurrentUser().getString( "driverOrPassenger" ).equals( "passenger" ) )
            {
                Intent intent = new Intent( context , ShowVehicleActivity.class );
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity( intent );
            }
            else
                Toast.makeText( context , "You are a driver, you cannot enter show vehicle" , Toast.LENGTH_LONG ).show();
        }

        // Bilkent Landmarks
        else if ( item.getItemId() == R.id.nav_landmarks )
        {
            Intent intent = new Intent( context , BilkentLandmarksActivity.class );
            context.startActivity( intent );
        }

        // Search Profiles
        else if ( item.getItemId() == R.id.nav_search_profiles )
        {
            Intent intent = new Intent( context , SearchProfileActivity.class );
            context.startActivity( intent );
        }

        // Feedback
        else if ( item.getItemId() == R.id.nav_feedback )
        {
            Intent intent = new Intent( context , FeedbackAndReportActivity.class );
            context.startActivity( intent );
        }

        // Logging out
        else if ( item.getItemId() == R.id.nav_log_out )
        {
            final Intent intent = new Intent( context , SignInActivity.class );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ParseUser.getCurrentUser().put( "driverOrPassenger" , "passenger" );
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    ParseUser.logOut();
                    context.startActivity( intent );
                }
            });

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}