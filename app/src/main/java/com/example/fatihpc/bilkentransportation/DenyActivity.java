package com.example.fatihpc.bilkentransportation;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DenyActivity extends AppCompatActivity
{
    // properties
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deny);
        getSupportActionBar().hide();

        // starting timer and directing to ShowVehicleActivity.
        countDownTimer = new CountDownTimer( 3000 , 1000 )
        {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish()
            {
                Intent intent = new Intent( getApplicationContext() , ShowVehicleActivity.class );
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( intent );
            }
        }.start();
    }

}