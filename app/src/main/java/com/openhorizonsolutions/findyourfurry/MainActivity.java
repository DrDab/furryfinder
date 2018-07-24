package com.openhorizonsolutions.findyourfurry;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import furrylib.FinderUtils;

import furrylib.Furry;
import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity
{

    private TextView furryCountView;
    private TextView latitudeView;
    private TextView longitudeView;

    private TextView furryName;
    private TextView furryDescription;
    private TextView furryDistance;

    private SimpleLocation location;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!DataStore.verifyStoragePermissions(this))
        {
            AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog1.setTitle("Warning! (DON'T CLOSE)");
            alertDialog1.setMessage("Please go into Settings > Apps > \"Find Your Furry\" > Permissions and check Storage and Location.");
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.show();
            return;
        }

        setContentView(R.layout.activity_main);

        furryCountView = (TextView) findViewById(R.id.furryCountTeext);
        latitudeView = (TextView) findViewById(R.id.latitudeText);
        longitudeView = (TextView) findViewById(R.id.longitudeText);
        furryName = (TextView) findViewById(R.id.furryName);
        furryDescription = (TextView) findViewById(R.id.furryDescription);
        furryDistance = (TextView) findViewById(R.id.furryDistance);

        //location = new SimpleLocation(this, true, false, 1000);
        location = new SimpleLocation(this);

        location.beginUpdates();

        try
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    for(;;)
                    {
                        DataStore.latitude = location.getLatitude();
                        DataStore.longitude = location.getLongitude();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                latitudeView.setText(DataStore.latitude + "°");
                                longitudeView.setText(DataStore.longitude + "°");
                            }
                        });
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }
            ).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        fetchFurryJSONData();
        keepFurryTallyCount();
    }

    public void keepFurryTallyCount()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                for(;;)
                {
                    if (DataStore.downloadSuccess)
                    {
                        DataStore.withinRange = FinderUtils.getFurryListWithinSearchRadius(DataStore.furryList, DataStore.latitude, DataStore.longitude, 20.0);
                        DataStore.withinRangeString = getFurryListAsPreviewString(DataStore.withinRange, DataStore.latitude, DataStore.longitude);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (DataStore.withinRange.size() == 0)
                                {
                                    furryName.setText("No furries nearby :3");
                                    furryDescription.setText("");
                                    furryDistance.setText("");
                                }
                                else
                                {
                                    furryName.setText(DataStore.withinRange.get(0).getUserName());
                                    furryDescription.setText(DataStore.withinRange.get(0).getDescription());
                                    furryDistance.setText(DataStore.withinRange.get(0).distanceFromCoords(DataStore.latitude, DataStore.longitude) + " miles away");
                                }
                                furryCountView.setText(DataStore.withinRange.size() + "");
                            }
                        });
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void fetchFurryJSONData()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while(DataStore.furryJSONData == null)
                {
                    try
                    {
                        DataStore.furryJSONData = FinderUtils.getJSONData();
                        DataStore.furryList = FinderUtils.getFurryList(DataStore.furryJSONData);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                Log.d("MainActivity", "Fetching furry data successful!");
                DataStore.downloadSuccess = true;
            }
        }).start();
    }


    public ArrayList<String> getFurryListAsPreviewString(ArrayList<Furry> e, double latitude, double longitude)
    {
        ArrayList<String> tmpLst = new ArrayList<String>();
        DecimalFormat df = new DecimalFormat("#.##");
        for(Furry furry : e)
        {
            tmpLst.add(furry.getUserName() + ", " + df.format(furry.distanceFromCoords(latitude, longitude)) + " miles away");
        }
        return tmpLst;
    }

    public void launchFurryList(View vue)
    {
        startActivity(new Intent(this, FurryList.class));
    }

}
