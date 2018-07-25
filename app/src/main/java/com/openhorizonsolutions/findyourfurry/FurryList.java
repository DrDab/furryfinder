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
import android.widget.ListView;

import java.text.DecimalFormat;

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
                alertDialog.setTitle("Furry Information");
                if (DataStore.withinRange.size() >= 1)
                {
                    final Furry furry = DataStore.withinRange.get(position);
                    String s = "";
                    s += "ID: " + furry.getID() + "\n";
                    s += "Username: " + furry.getUserName() + "\n";
                    s += "Description: " + furry.getDescription() + "\n";
                    s += "Profile: http://furrymap.net" + furry.getProfile() + "\n";
                    s += "Distance: " + new DecimalFormat("#.##").format(furry.distanceFromCoords(DataStore.latitude, DataStore.longitude)) + " miles\n";
                    s += "Latitude: " + furry.getLatitude() + "°\n";
                    s += "Longitude: " + furry.getLongitude() + "°\n";
                    alertDialog.setMessage(s);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Navigate", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + furry.getLatitude() + "," + furry.getLongitude() + ""));
                            startActivity(intent);
                        }
                    });
                    alertDialog.show();
                }
                else
                {
                    alertDialog.setMessage("No Furries Yet");
                    alertDialog.show();
                }

            }
        });

    }
}
