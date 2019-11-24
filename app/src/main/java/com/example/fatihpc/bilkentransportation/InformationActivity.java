package com.example.fatihpc.bilkentransportation;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InformationActivity extends AppCompatActivity
{
    private ViewPager mSlideViewPager;
    private com.example.fatihpc.bilkentransportation.SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);

        sliderAdapter = new com.example.fatihpc.bilkentransportation.SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
