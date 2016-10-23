package com.example.noname.freelancerproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String LOCATION_KEY = "location";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public JSONArray jArray = null;
    public JSONObject jsonObj = null;
    ListView mainList;
    public static Handler mUiHandler = null;

    private static GoogleApiClient mGoogleApiClient;
    private Context ctx;
    private Location mCurrentLocation;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.noname.freelancerproject.R.layout.activity_main);

        ctx = this;
        mGoogleApiClient = instantiateGoogleAPIClient();

        updateValuesFromBundle(savedInstanceState);


        // Create service incoming handler
        mUiHandler = new Handler() // Receive messages from service class
        {


            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case 0:
                            // add the status which came from service and show on GUI
                            Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ctx, DetailActivity.class);
                            JSONObject jsonObjDetail = jArray.getJSONObject(0);
                            intent.putExtra("clickedItem", jsonObjDetail.toString());
                            ctx.startActivity(intent);
                            break;

                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        // Load state
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        mainList = (ListView) findViewById(com.example.noname.freelancerproject.R.id.checkableList);

        try {
            //Get Data From internal file, if doesn't exist copy resource to internal and then use it .

            if (!pref.contains("json")) {
                jsonObj = openJsonFile("items.json");
            } else {
                jsonObj = new JSONObject(pref.getString("json", null));
            }
            JSONObject jObjectResult = jsonObj.getJSONObject("Items");
            this.jArray = jObjectResult.getJSONArray("Item");
            // Bind Data and pass the json object read from a file to the adapter
            MainViewAdapter customListViewAdapter = new MainViewAdapter(this, jsonObj);
            mainList.setAdapter(customListViewAdapter);


            // Service communication

            //startService(new Intent(this, MyService.class));
            onClickStartService();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        checkGPS();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //stopService(new Intent(this, MyService.class));
        Toast.makeText(this, "App Stopped", Toast.LENGTH_LONG).show();
    }

    private JSONObject openJsonFile(String filename) throws IOException, JSONException {
        InputStream inputStream = getResources().openRawResource(com.example.noname.freelancerproject.R.raw.items);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        ctr = inputStream.read();
        while (ctr != -1) {
            byteArrayOutputStream.write(ctr);
            ctr = inputStream.read();
        }
        inputStream.close();
        // Parse the data into json object to get original data in form of json.
        jsonObj = new JSONObject(byteArrayOutputStream.toString());
        return jsonObj;
    }

    //start the service
    public void onClickStartService() {
        //start the service from here //MyService is your service class name
        startService(new Intent(this, MyService.class));
    }

    //Stop the started service
    public void onClickStopService() {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        stopService(new Intent(this, MyService.class));
    }

    //send message to service
    public void onClickSendMessage() {
        //only we need a handler to send message to any component.
        //here we will get the handler from the service first, then
        //we will send a message to the service.

        if (null != MyService.mMyServiceHandler) {
            //first build the message and send.
            //put a integer value here and get it from the service handler
            //For Example: lets use 0 (msg.what = 0;) for getting service running status from the service
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "Add your Extra Meaage Here"; // you can put extra message here
            MyService.mMyServiceHandler.sendMessage(msg);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Sorry, can`t access Google API", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        Toast.makeText(MainActivity.this, "Start fetching locations", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Google API client disconnected", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    private void startLocationUpdates() {
        final LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    private GoogleApiClient instantiateGoogleAPIClient() {
        GoogleApiClient result = new GoogleApiClient.Builder(ctx)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        return result;
    }

    private void checkGPS() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) &&
                !((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .isProviderEnabled(LocationManager.GPS_PROVIDER))
            enableGPS();
    }

    private void enableGPS() {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    (Activity) ctx, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Location Manager");
                        builder.setMessage("Would you like to enable GPS?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.create().show();
                        break;
                }
            }
        });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.keySet().contains(LOCATION_KEY))
            mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
    }

}
