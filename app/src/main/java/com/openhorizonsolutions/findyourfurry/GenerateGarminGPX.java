package com.openhorizonsolutions.findyourfurry;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import furrylib.FinderUtils;
import furrylib.Furry;

public class GenerateGarminGPX extends AppCompatActivity
{

    private EditText generatorLat;
    private EditText generatorLon;
    private EditText generatorRan;
    private TextView rangeUnitText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_garmin_gpx);
        setTitle("GPX Creator");

        generatorLat = (EditText) findViewById(R.id.generatorLatitudeForm);
        generatorLon = (EditText) findViewById(R.id.generatorLongitudeForm);
        generatorRan = (EditText) findViewById(R.id.generatorRangeForm);
        rangeUnitText = (TextView) findViewById(R.id.generatorTextRng);

        rangeUnitText.setText(DataStore.useMetrics ? "RANGE (km)" : "RANGE (mi)");
        generatorLat.setText(DataStore.latitude + "");
        generatorLon.setText(DataStore.longitude + "");
        generatorRan.setText(DataStore.searchRadius + "");
    }

    public void createGPX(View vue)
    {
        double tmpLat = 0.0;
        double tmpLon = 0.0;
        double tmpRange = 0.0;

        try
        {
            tmpLat = Double.parseDouble(generatorLat.getText().toString());
            tmpLon = Double.parseDouble(generatorLon.getText().toString());
            tmpRange = Double.parseDouble(generatorRan.getText().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("GPX file generation failed.")
                    .setMessage("All forms must have valid numerical data.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
            return;
        }



        int sizeOfGPX = writeGPXFile(tmpLat, tmpLon, tmpRange);

        if(sizeOfGPX != -1)
        {
            new AlertDialog.Builder(this)
                    .setTitle("GPX file generated successfully")
                    .setMessage("Generated GPX file containing " + sizeOfGPX + " furries to location: \"FindYourFurry/Waypoints FurryMap.gpx\".")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            finish();
                        }
                    })
                    .show();
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("GPX file generation failed.")
                    .setMessage("Something went wrong!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    }

    public int writeGPXFile(double latitude, double longitude, double range)
    {
        try
        {
            File writeDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
            if (!writeDirectory.exists())
            {
                writeDirectory.mkdir();
            }

            File log = new File(writeDirectory, "Waypoints FurryMap.gpx");

            if(!log.exists())
            {
                log.createNewFile();
            }
            else
            {
                log.delete();
                log = new File(writeDirectory, "Waypoints FurryMap.gpx");
                log.createNewFile();
            }


            ArrayList<Furry> sorted = null;
            if (DataStore.useMetrics)
            {
                sorted = FinderUtils.getFurryListWithinSearchRadiusMetric(DataStore.furryList, latitude, longitude, range);
            }
            else
            {
                sorted = FinderUtils.getFurryListWithinSearchRadius(DataStore.furryList, latitude, longitude, range);
            }

            PrintWriter pw = new PrintWriter(new FileWriter(log));
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            String iso8601Cur = df.format(new Date());
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:wptx1=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"fenix\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www8.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackStatsExtension/v1 http://www8.garmin.com/xmlschemas/TrackStatsExtension.xsd http://www.garmin.com/xmlschemas/WaypointExtension/v1 http://www8.garmin.com/xmlschemas/WaypointExtensionv1.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\"><metadata><link href=\"http://www.garmin.com\"><text>Garmin International</text></link><time>" + iso8601Cur + "</time></metadata>");
            for(Furry e : sorted)
            {
                double curLat = e.getLatitude();
                double curLon = e.getLongitude();
                String userName = e.getUserName();
                pw.println("<wpt lat=\"" + curLat + "\" lon=\"" + curLon + "\"><ele>0.0</ele><time>" + iso8601Cur + "</time><name>" + userName + "</name><sym>Flag, Blue</sym></wpt>");
            }
            pw.println("</gpx>");
            pw.close();
            return sorted.size();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return -1;
        }
    }
}
