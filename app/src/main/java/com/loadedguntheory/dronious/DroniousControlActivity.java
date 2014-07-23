package com.loadedguntheory.dronious;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.codeminders.ardrone.ARDrone;

import java.io.IOException;
import java.net.UnknownHostException;


public class DroniousControlActivity extends Activity {

    private static final String TAG = "DroniousControlActivity";
    private MenuItem actionConnected;
    private ARDrone drone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dronius_control);

        try {
            drone = new ARDrone();
        } catch(UnknownHostException e) {
            Log.e(TAG, "Unable to find drone.", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTask<Object, Object, Boolean>() {

            @Override
            protected Boolean doInBackground(Object... objects) {
                try {
                    drone.connect();
                    drone.waitForReady(5000);
                    return Boolean.TRUE;
                } catch(IOException e) {
                    Log.e(TAG, "Unable to connect to drone.", e);
                }
                return Boolean.TRUE;
            }
            @Override
            protected void onPostExecute(Boolean connected) {
                Log.e(TAG, "Connected " + connected);
                actionConnected.setChecked(connected);
            }
        }.execute();
    }

    @Override
    protected void onPause() {
        new AsyncTask<Object, Object, Boolean>() {

            @Override
            protected Boolean doInBackground(Object... objects) {
                try {
                    drone.disconnect();
                    return Boolean.TRUE;
                } catch(IOException e) {
                    Log.e(TAG, "Unable to disconnect from drone.", e);
                }
                return Boolean.FALSE;
            }
        }.execute();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dronius_control, menu);
        actionConnected = menu.findItem(R.id.action_connected);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_connected) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onToggleOneClicked(View view) {
        // Is the toggle on?
        boolean on = ((Switch) view).isChecked();

        if (on) {
            Log.d(TAG, "Toggled on");
            new AsyncTask<Object, Object, Boolean>() {

                @Override
                protected Boolean doInBackground(Object... objects) {
                    try {
                        drone.waitForReady(300);
                        drone.takeOff();
                        drone.hover();
                        return Boolean.TRUE;
                    } catch(IOException e) {
                        Log.e(TAG, "Unable to connect to drone.", e);
                    }
                    return Boolean.TRUE;
                }
                @Override
                protected void onPostExecute(Boolean connected) {
                    Log.e(TAG, "Connected " + connected);
                    actionConnected.setChecked(connected);
                }
            }.execute();

        } else {
            Log.d(TAG, "Toggled off");
            new AsyncTask<Object, Object, Boolean>() {

                @Override
                protected Boolean doInBackground(Object... objects) {
                    try {
                        drone.waitForReady(300);
                        drone.land();
                        return Boolean.TRUE;
                    } catch(IOException e) {
                        Log.e(TAG, "Unable to connect to drone.", e);
                    }
                    return Boolean.TRUE;
                }
                @Override
                protected void onPostExecute(Boolean connected) {
                    Log.e(TAG, "Connected " + connected);
                    actionConnected.setChecked(connected);
                }
            }.execute();

            // should it immediately land?
        }
    }
}
