package com.openhorizonsolutions.findyourfurry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import furrylib.FinderUtils;
import furrylib.Furry;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity
{
    private TextView furryCountView;
    private TextView latitudeView;
    private TextView longitudeView;

    private TextView furryCountDescription;

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

        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
        {
            String carrier = tm.getNetworkOperatorName();
            if (carrier != null)
            {
                File readDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
                if (!readDirectory.exists())
                {
                    readDirectory.mkdir();
                }
                final File notice = new File(readDirectory, "notice");
                if (carrier.toLowerCase().indexOf("verizon") != -1 && !notice.exists())
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater adbInflater = LayoutInflater.from(this);
                    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
                    final CheckBox dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
                    adb.setView(eulaLayout);
                    final AlertDialog alertDialog1 = adb.create();
                    alertDialog1.setTitle("Verizon User - Please Read!");
                    alertDialog1.setMessage("Verizon is a carrier that supports the censorship of free information and violation of Internet freedoms, such as the right to private communications and neutral speeds, for its' own financial gain. It is strongly advised that you switch to a carrier that respects your electronic freedoms. Thank you for using my software! ;-)");
                    alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            if (dontShowAgain.isChecked())
                            {
                                try
                                {
                                    notice.createNewFile();
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            alertDialog1.dismiss();
                        }
                    });
                    alertDialog1.show();
                }
            }
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);

        try
        {
            DataStore.readSettingsData("furryconfig.coda");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        furryCountView = (TextView) findViewById(R.id.furryCountTeext);
        latitudeView = (TextView) findViewById(R.id.latitudeText);
        longitudeView = (TextView) findViewById(R.id.longitudeText);
        furryName = (TextView) findViewById(R.id.furryName);
        furryDescription = (TextView) findViewById(R.id.furryDescription);
        furryDistance = (TextView) findViewById(R.id.furryDistance);
        furryCountDescription = (TextView) findViewById(R.id.textView2);

        furryCountDescription.setText(String.format("furries within %5.2f %s of you", DataStore.searchRadius, (DataStore.useMetrics ? "km" : "miles")));

        location = new SimpleLocation(this, DataStore.useGPS, false, 1000);
        location.beginUpdates();

        if (DataStore.furryDataFileExists("combined.json"))
        {
            Log.d("FindYourFurry", "File exists!");
            try
            {
                DataStore.readFurryDataFromJsonFile("combined.json");
                Log.d("FindYourFurry", "Done reading JSON data");
                DataStore.downloadSuccess = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.d("FindYourFurry", "File doesn't exist!");
            fetchFurryJSONData();
        }
        keepFurryTallyCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_furrymenu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_search)
        {
            startActivity(new Intent(this, Search.class));
        }
        else if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, Settings.class));
        }
        else if (id == R.id.action_about)
        {
            startActivity(new Intent(this, About.class));
        }
        else if (id == R.id.action_creategpx)
        {
            startActivity(new Intent(this, GenerateGarminGPX.class));
        }
        else if (id == R.id.action_syncfurrylist)
        {
            DataStore.downloadSuccess = false;
            furryCountView.setText("Loading...");
            furryName.setText("Loading...");
            furryDescription.setText("Loading...");
            furryDistance.setText("Loading...");
            fetchFurryJSONData();
        }
        return super.onOptionsItemSelected(item);
    }

    public synchronized void forceFurryCountUpdate()
    {
        DataStore.latitude = location.getLatitude();
        DataStore.longitude = location.getLongitude();

        if (DataStore.downloadSuccess)
        {
            DataStore.withinRange = DataStore.useMetrics ? FinderUtils.getFurryListWithinSearchRadiusMetric(DataStore.furryList, DataStore.latitude, DataStore.longitude, DataStore.searchRadius) : FinderUtils.getFurryListWithinSearchRadius(DataStore.furryList, DataStore.latitude, DataStore.longitude, DataStore.searchRadius);
            DataStore.withinRangeString = DataStore.getFurryListAsPreviewString(DataStore.withinRange, DataStore.latitude, DataStore.longitude);
        }

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                latitudeView.setText(String.format("%8.5f°", DataStore.latitude));
                longitudeView.setText(String.format("%8.5f°", DataStore.longitude));
                if (DataStore.downloadSuccess)
                {
                    furryCountDescription.setText(String.format("furries within %5.2f %s of you", DataStore.searchRadius, (DataStore.useMetrics ? "km" : "miles")));
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
                        furryDistance.setText(String.format("%5.2f %s away", (DataStore.useMetrics ? DataStore.withinRange.get(0).distanceFromCoordsMetric(DataStore.latitude, DataStore.longitude) : DataStore.withinRange.get(0).distanceFromCoords(DataStore.latitude, DataStore.longitude)), (DataStore.useMetrics ? "km" : "miles")));
                    }
                    furryCountView.setText(DataStore.withinRange.size() + "");
                }
            }
        });
    }



    public void keepFurryTallyCount()
    {
        final Thread t1 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (;;)
                {
                    if (Thread.interrupted())
                    {
                        forceFurryCountUpdate();
                    }
                }
            }
        });

        t1.start();

        location.setListener(new SimpleLocation.Listener()
        {
            @Override
            public void onPositionChanged()
            {
                t1.interrupt();
            }
        });
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
                try
                {
                    DataStore.writeFurryDataToJsonFile("combined.json");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                Log.d("MainActivity", "Fetching furry data successful!");
                DataStore.downloadSuccess = true;
            }
        }).start();
    }


    public void launchFurryList(View vue)
    {
        Intent furryListIntent = new Intent(this, FurryList.class);
        furryListIntent.putExtra("listmode", ListMode.LIVE_MODE);
        startActivity(furryListIntent);
    }

}
