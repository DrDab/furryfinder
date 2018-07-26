package com.openhorizonsolutions.findyourfurry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class Settings extends AppCompatActivity
{
    private EditText radiusForm;
    private CheckBox useGPSCB;
    private CheckBox useMetricCB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        radiusForm = (EditText) findViewById(R.id.searchRadiusForm);
        useGPSCB = (CheckBox) findViewById(R.id.gpsCheckBox);
        useMetricCB = (CheckBox) findViewById(R.id.metricCheckBox);

        radiusForm.setText(DataStore.searchRadius + "");
        useGPSCB.setChecked(DataStore.useGPS);
        useMetricCB.setChecked(DataStore.useMetrics);
    }

    public void confirmSettings(View vue)
    {
        try
        {
            DataStore.searchRadius = Double.parseDouble(radiusForm.getText().toString());
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this, "Please enter a number for the search radius.", Toast.LENGTH_LONG).show();
            return;
        }

        DataStore.useGPS = useGPSCB.isChecked();
        DataStore.useMetrics = useMetricCB.isChecked();

        try
        {
            DataStore.writeSettingsData("furryconfig.coda", DataStore.searchRadius, DataStore.useGPS, DataStore.useMetrics);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        finish();
    }
}
