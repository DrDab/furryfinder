package com.openhorizonsolutions.findyourfurry;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import furrylib.FinderUtils;
import furrylib.Furry;

public class Search extends AppCompatActivity
{
    private EditText latitudeBox;
    private EditText longitudeBox;
    private EditText radiusBox;
    private EditText nameBox;
    private TextView radiusText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("Search Furries ^w^");

        latitudeBox = (EditText) findViewById(R.id.searchLatitude);
        longitudeBox = (EditText) findViewById(R.id.searchLongitude);
        radiusBox = (EditText) findViewById(R.id.searchRadiusForm);
        nameBox = (EditText) findViewById(R.id.searchName);
        radiusText = (TextView) findViewById(R.id.searchRadiusTextView);

        radiusText.setText(DataStore.useMetrics ? "Radius (km)" : "Radius (mi)");
        latitudeBox.setText(DataStore.latitude + "");
        longitudeBox.setText(DataStore.longitude + "");
        radiusBox.setText(DataStore.searchRadius + "");
    }

    public void onSearchClicked(View vue)
    {
        boolean satisfied = true;
        double latitude = 0.0;
        double longitude = 0.0;
        double radius = 0.0;
        String name = "";

        try
        {
            latitude = Double.parseDouble(latitudeBox.getText().toString());
        }
        catch (Exception e)
        {
            satisfied = false;
        }

        if (satisfied)
        {
            try
            {
                longitude = Double.parseDouble(longitudeBox.getText().toString());
            }
            catch (Exception e)
            {
                satisfied = false;
            }
        }

        if (satisfied)
        {
            try
            {
                radius = Double.parseDouble(radiusBox.getText().toString());
            }
            catch (Exception e)
            {
                satisfied = false;
            }
        }

        if (satisfied)
        {
            name = nameBox.getText().toString().toLowerCase();
        }

        if (satisfied)
        {
            DataStore.withinRangeSearch.clear();
            DataStore.withinRangeStringSearch.clear();

            Intent listIntent = new Intent(this, FurryList.class);
            listIntent.putExtra("listmode", ListMode.SEARCH_MODE);

            ArrayList<Furry> withinSearchRadius = DataStore.useMetrics ? FinderUtils.getFurryListWithinSearchRadiusMetric(DataStore.furryList, latitude, longitude, radius) : FinderUtils.getFurryListWithinSearchRadius(DataStore.furryList, latitude, longitude, radius);
            if (name.equals(""))
            {
                DataStore.withinRangeSearch = withinSearchRadius;
                DataStore.withinRangeStringSearch = DataStore.getFurryListAsPreviewString(withinSearchRadius, latitude, longitude);
            }
            else
            {
                for (Furry furry : withinSearchRadius)
                {
                    String username = furry.getUserName().toLowerCase();
                    if (username.contains(name))
                    {
                        DataStore.withinRangeSearch.add(furry);
                    }
                }
                DataStore.withinRangeStringSearch = DataStore.getFurryListAsPreviewString(DataStore.withinRangeSearch, latitude, longitude);
            }

            startActivity(listIntent);
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("All forms must have valid data.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    }
}
