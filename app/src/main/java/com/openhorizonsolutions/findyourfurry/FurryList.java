package com.openhorizonsolutions.findyourfurry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    }
}
