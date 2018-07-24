package com.openhorizonsolutions.findyourfurry;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

import furrylib.Furry;

public class DataStore
{
    public static String furryJSONData = null;
    public static ArrayList<Furry> withinRange = new ArrayList<Furry>();
    public static ArrayList<Furry> furryList = new ArrayList<Furry>();
    public static ArrayList<String> withinRangeString = new ArrayList<String>();

    public static double latitude = 0.0;
    public static double longitude = 0.0;

    public static boolean downloadSuccess = false;

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

}
