package furrylib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FinderUtils
{

    /**
     * A function to return an ArrayList of instances of Furry classes from scraped OpenStreetMap GeoJSON Data.
     *
     * @param JSONData
     * @return an ArrayList of instances of the Furry class read from the JSON data.
     */
    @SuppressWarnings("unused")
    public static ArrayList<Furry> getFurryList(String JSONData)
    {
        try
        {
            ArrayList<Furry> tmpLst = new ArrayList<Furry>();

            JSONObject obj = new JSONObject(JSONData);
            JSONObject combined = (JSONObject) obj.get("combined");
            JSONObject geoJSONData = combined.getJSONObject("geojson");
            JSONArray furryList = geoJSONData.getJSONArray("features");
            for (int i = 0; i < furryList.length(); i++)
            {
                JSONArray furryProfile = furryList.getJSONArray(i);
                double longitude = furryProfile.getDouble(0);
                double latitude = furryProfile.getDouble(1);
                String id = furryProfile.getString(2);
                String description = StringUtils.unescapeHtml3(furryProfile.getString(3));
                int opacityFactor = furryProfile.getInt(4);
                String userName = furryProfile.getString(5);
                String profile = furryProfile.getString(6);
                String profilePicture = furryProfile.getInt(7) == 0 ? null : "/images/avatar/" + furryProfile.getInt(7) + ".png";
                tmpLst.add(new Furry(latitude, longitude, id, userName, description, profile, profilePicture));
            }
            return tmpLst;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A function to get a sorted ArrayList<Furry> of furries within a defined search radius of a pair of coordinates.
     * The list is sorted by distance from the pair of coordinates.
     * The input data can be provided via JSON file.
     *
     * @param JSONData
     * @param latitude (the latitude of the defined location)
     * @param longitude (the longitude of the defined location)
     * @param searchRadius (the search radius in miles)
     * @return a sorted ArrayList<Furry> of furries within the search radius
     */
    public static ArrayList<Furry> getFurryListWithinSearchRadius(String JSONData, double latitude, double longitude, double searchRadius)
    {
        ArrayList<Furry> rawList = FinderUtils.getFurryList(JSONData);
        ArrayList<FurryDistanceHandler> tmpLst = new ArrayList<FurryDistanceHandler>();
        ArrayList<Furry> retList = new ArrayList<Furry>();
        for (Furry e : rawList)
        {
            double distance = e.distanceFromCoords(latitude, longitude);
            if (distance <= searchRadius)
            {
                tmpLst.add(new FurryDistanceHandler(e, distance));
            }
        }

        Collections.sort(tmpLst);

        for (FurryDistanceHandler fdh : tmpLst)
        {
            retList.add(fdh.getFurry());
        }
        return retList;
    }


    /**
     * A function to get a sorted ArrayList<Furry> of furries within a defined search radius of a pair of coordinates.
     * The list is sorted by distance from the pair of coordinates.
     * The input data can be provided via an existing sorted or unsorted ArrayList<Furry> containing a list of
     * Furry classes worldwide.
     *
     * @param furryList (an ArrayList<Furry> of furries worldwide)
     * @param latitude (the latitude of the defined location)
     * @param longitude (the longitude of the defined location)
     * @param searchRadius (the search radius in miles)
     * @return a sorted ArrayList<Furry> of furries within the search radius
     */
    public static ArrayList<Furry> getFurryListWithinSearchRadius(ArrayList<Furry> furryList, double latitude, double longitude, double searchRadius)
    {
        ArrayList<FurryDistanceHandler> tmpLst = new ArrayList<FurryDistanceHandler>();
        ArrayList<Furry> retList = new ArrayList<Furry>();
        for (Furry e : furryList)
        {
            double distance = e.distanceFromCoords(latitude, longitude);
            if (distance <= searchRadius)
            {
                tmpLst.add(new FurryDistanceHandler(e, distance));
            }
        }

        Collections.sort(tmpLst);

        for (FurryDistanceHandler fdh : tmpLst)
        {
            retList.add(fdh.getFurry());
        }
        return retList;
    }

    /**
     * A function to get a sorted ArrayList<Furry> of furries within a defined search radius in kilometers of a pair of coordinates.
     * The list is sorted by distance from the pair of coordinates.
     * The input data can be provided via JSON file.
     *
     * @param JSONData
     * @param latitude (the latitude of the defined location)
     * @param longitude (the longitude of the defined location)
     * @param searchRadius (the search radius in kilometers)
     * @return a sorted ArrayList<Furry> of furries within the search radius
     */
    public static ArrayList<Furry> getFurryListWithinSearchRadiusMetric(String JSONData, double latitude, double longitude, double searchRadius)
    {
        ArrayList<Furry> rawList = FinderUtils.getFurryList(JSONData);
        ArrayList<FurryDistanceHandler> tmpLst = new ArrayList<FurryDistanceHandler>();
        ArrayList<Furry> retList = new ArrayList<Furry>();
        for (Furry e : rawList)
        {
            double distance = e.distanceFromCoordsMetric(latitude, longitude);
            if (distance <= searchRadius)
            {
                tmpLst.add(new FurryDistanceHandler(e, distance));
            }
        }

        Collections.sort(tmpLst);

        for (FurryDistanceHandler fdh : tmpLst)
        {
            retList.add(fdh.getFurry());
        }
        return retList;
    }


    /**
     * A function to get a sorted ArrayList<Furry> of furries within a defined search radius in kilometers of a pair of coordinates.
     * The list is sorted by distance from the pair of coordinates.
     * The input data can be provided via an existing sorted or unsorted ArrayList<Furry> containing a list of
     * Furry classes worldwide.
     *
     * @param furryList (an ArrayList<Furry> of furries worldwide)
     * @param latitude (the latitude of the defined location)
     * @param longitude (the longitude of the defined location)
     * @param searchRadius (the search radius in kilometers)
     * @return a sorted ArrayList<Furry> of furries within the search radius
     */
    public static ArrayList<Furry> getFurryListWithinSearchRadiusMetric(ArrayList<Furry> furryList, double latitude, double longitude, double searchRadius)
    {
        ArrayList<FurryDistanceHandler> tmpLst = new ArrayList<FurryDistanceHandler>();
        ArrayList<Furry> retList = new ArrayList<Furry>();
        for (Furry e : furryList)
        {
            double distance = e.distanceFromCoordsMetric(latitude, longitude);
            if (distance <= searchRadius)
            {
                tmpLst.add(new FurryDistanceHandler(e, distance));
            }
        }

        Collections.sort(tmpLst);

        for (FurryDistanceHandler fdh : tmpLst)
        {
            retList.add(fdh.getFurry());
        }
        return retList;
    }

    /**
     * A function to return online GeoJSON data containing a list of furries from furrymap.net.
     *
     * @return a String containing GeoJSON data.
     */
    public static String getJSONData()
    {
        try
        {
            String url_base = "https://furrymap.net/en/marker/list/type/combined";
            URL jsonURL = new URL(url_base);
            URLConnection yc = jsonURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            String inputLine;
            String returnData = "";
            while ((inputLine = in.readLine()) != null)
            {
                returnData += inputLine;
            }
            return returnData;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A function to return online GeoJSON data containing a list of furries from a locally-stored JSON file.
     *
     * @return a String containing GeoJSON data.
     */
    public static String getJSONDataFromFile(String location)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(location));
            String s = "";
            String tmp = "";
            try
            {
                while((tmp = br.readLine()) != null)
                {
                    s += tmp;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return s;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

class FurryDistanceHandler implements Comparable<FurryDistanceHandler>
{
    private Furry furry;
    private double distance;

    public FurryDistanceHandler(Furry furry, double distance)
    {
        this.furry = furry;
        this.distance = distance;
    }

    public Furry getFurry()
    {
        return furry;
    }

    public double getDistance()
    {
        return distance;
    }

    @Override
    public int compareTo(FurryDistanceHandler o)
    {
        return ((Double) distance).compareTo((Double) o.getDistance());
    }

}
