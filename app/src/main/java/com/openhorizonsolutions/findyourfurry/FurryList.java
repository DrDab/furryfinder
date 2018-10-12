package com.openhorizonsolutions.findyourfurry;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import furrylib.Furry;

public class FurryList extends AppCompatActivity
{

    private ArrayAdapter<String> adapter;
    private ListView contestList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furry_list);

        setTitle("Furry List! :3");

        contestList = (ListView) findViewById(R.id.furryListView);


        adapter = new ArrayAdapter <String>
                (FurryList.this, android.R.layout.simple_list_item_1,
                        DataStore.withinRangeString);

        contestList.setAdapter(adapter);

        contestList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int position, long arg3)
            {
                // position = position selected
                final AlertDialog alertDialog = new AlertDialog.Builder(FurryList.this).create();
                boolean safetyCheck = false;
                try
                {
                    DataStore.withinRange.get(position);
                }
                catch (NullPointerException npe)
                {
                    safetyCheck = true;
                }
                alertDialog.setTitle("Furry Information");
                if (!safetyCheck)
                {
                    final Furry furry = DataStore.withinRange.get(position);
                    // TODO: add profile picture in the future?
                    // ImageView image = new ImageView(FurryList.this);
                    // image.setImageResource(R.drawable.furrymap);
                    // alertDialog.setView(image);
                    String s = "";
                    s += "ID: " + furry.getID() + "\n";
                    s += "Username: " + furry.getUserName() + "\n";
                    s += "Description: " + furry.getDescription() + "\n";
                    s += "Profile: http://furrymap.net" + furry.getProfile() + "\n";
                    s += "Distance: " + String.format("%5.2f %s\n", (DataStore.useMetrics ? furry.distanceFromCoordsMetric(DataStore.latitude, DataStore.longitude) : furry.distanceFromCoords(DataStore.latitude, DataStore.longitude)), (DataStore.useMetrics ? " km" : " miles"));
                    s += "Latitude: " + furry.getLatitude() + "°\n";
                    s += "Longitude: " + furry.getLongitude() + "°\n";
                    s += "Direction: " + String.format("%5.1f°", furry.angleFromCoords(DataStore.latitude, DataStore.longitude));
                    alertDialog.setMessage(s);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Navigate", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("geo:0,0?q=" + furry.getLatitude() +  "," + furry.getLongitude() +  "(" + furry.getUserName() + ")"));
                            startActivity(intent);
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Profile", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://furrymap.net" + furry.getProfile()));
                            startActivity(intent);
                        }
                    });
                    alertDialog.show();
                }
                else
                {
                    alertDialog.setMessage("That furry doesn't exist!");
                    alertDialog.show();
                }

            }
        });

        new Thread(new Runnable()
        {
            public void run()
            {
                for(;;)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            int index = contestList.getFirstVisiblePosition(); //This changed
                            View v = contestList.getChildAt(0);
                            int top = (v == null) ? 0 : v.getTop();
                            adapter = new ArrayAdapter <String>
                                    (FurryList.this, android.R.layout.simple_list_item_1,
                                            DataStore.withinRangeString);
                            contestList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            contestList.setSelectionFromTop(index, top);
                        }
                    });
                    try
                    {
                        Thread.sleep(2000);
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
}
