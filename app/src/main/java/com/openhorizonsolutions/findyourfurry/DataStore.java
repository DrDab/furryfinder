package com.openhorizonsolutions.findyourfurry;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import furrylib.FinderUtils;
import furrylib.Furry;

public class DataStore
{
    public static String furryJSONData = null;
    public static ArrayList<Furry> withinRange = new ArrayList<Furry>();
    public static ArrayList<Furry> furryList = new ArrayList<Furry>();
    public static ArrayList<String> withinRangeString = new ArrayList<String>();

    public static ArrayList<Furry> withinRangeSearch = new ArrayList<Furry>();
    public static ArrayList<String> withinRangeStringSearch = new ArrayList<String>();

    public static double latitude = 0.0;
    public static double longitude = 0.0;

    public static double searchRadius = 20.0;
    public static boolean useGPS = true;
    public static boolean useMetrics = false;

    public static boolean downloadSuccess = false;
    public static boolean updatorThreadInit = false;

    public static boolean verifyStoragePermissions(Activity activity)
    {
        // Check if we have write permission
        int p = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int p2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int p3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int p4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        Log.d("FileIO", "Checking File I/O Permissions...");
        if (p != PackageManager.PERMISSION_GRANTED || p2 != PackageManager.PERMISSION_GRANTED || p3 != PackageManager.PERMISSION_GRANTED || p4 != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            Log.d("FileIO", "File permissions insufficient, requesting privileges...");
            return false;

        }
        return true;
    }   //verifyStoragePermissions

    public static ArrayList<String> getFurryListAsPreviewString(ArrayList<Furry> e, double latitude, double longitude)
    {
        ArrayList<String> tmpLst = new ArrayList<String>();
        for(Furry furry : e)
        {
            tmpLst.add(String.format("%s, %5.2f %s away", furry.getUserName(), (DataStore.useMetrics ? furry.distanceFromCoordsMetric(latitude, longitude) : furry.distanceFromCoords(latitude, longitude)), (DataStore.useMetrics ? "km" : "miles")));
        }
        return tmpLst;
    }

    public static boolean furryDataFileExists(String filename)
    {
        File writeDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
        File log = new File(writeDirectory, filename);
        return log.exists();
    }

    public static boolean writeFurryDataToJsonFile(String filename) throws IOException
    {
        File writeDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
        if (!writeDirectory.exists())
        {
            writeDirectory.mkdir();
        }
        if (furryJSONData == null)
        {
            return false;
        }
        else
        {
            File log = new File(writeDirectory, filename);
            if(!log.exists())
            {
                log.createNewFile();
            }
            PrintWriter madoka = new PrintWriter(new FileWriter(log));
            madoka.print(furryJSONData);
            madoka.flush();
            madoka.close();
            return true;
        }
    }

    public static boolean readFurryDataFromJsonFile(String filename) throws IOException
    {
        File readDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
        if (!readDirectory.exists())
        {
            readDirectory.mkdir();
        }
        File log = new File(readDirectory, filename);
        if (log.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(log));
                furryJSONData = br.readLine();
                DataStore.furryList = FinderUtils.getFurryList(DataStore.furryJSONData);
                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            return false;
        }
        return false;
    }

    public static boolean readSettingsData(String filename) throws IOException
    {
        File readDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
        if (!readDirectory.exists())
        {
            readDirectory.mkdir();
        }
        File log = new File(readDirectory, filename);
        if (log.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(log));
                searchRadius = Double.parseDouble(br.readLine());
                useGPS = br.readLine().contains("y");
                useMetrics = br.readLine().contains("y");
                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (NullPointerException e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            log.createNewFile();
            return false;
        }
        return false;
    }

    public static boolean writeSettingsData(String filename, double searchRadius, boolean useGPS, boolean useMetrics) throws IOException
    {
        File writeDirectory = new File(Environment.getExternalStorageDirectory(), "FindYourFurry");
        if (!writeDirectory.exists())
        {
            writeDirectory.mkdir();
        }

        File log = new File(writeDirectory, filename);

        if(!log.exists())
        {
            log.createNewFile();
        }

        PrintWriter madoka = new PrintWriter(new FileWriter(log));
        madoka.println(searchRadius + "");
        madoka.println(useGPS ? "y" : "n");
        madoka.println(useMetrics ? "y" : "n");

        madoka.flush();
        madoka.close();
        return true;
    }

}
