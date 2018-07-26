package com.openhorizonsolutions.findyourfurry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

public class About extends AppCompatActivity
{

    TextView buildDateView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About X3");

        Date buildDate = new Date(BuildConfig.TIMESTAMP);

        buildDateView = (TextView) findViewById(R.id.buildIDTextView);
        buildDateView.setText("Build Date: " + buildDate.getTime());
    }
}
