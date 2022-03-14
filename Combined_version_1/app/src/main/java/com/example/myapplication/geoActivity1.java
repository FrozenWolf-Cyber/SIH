package com.example.myapplication;

import static java.lang.Math.sqrt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class geoActivity1 extends AppCompatActivity {

    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        getCurrentLocation();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    getCurrentLocation();

                } else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                    .removeLocationUpdates(this);

                            if (locationResult != null && locationResult.getLocations().size() > 0) {

                                int index = locationResult.getLocations().size() - 1;
                                double latitude = locationResult.getLocations().get(index).getLatitude();
                                double longitude = locationResult.getLocations().get(index).getLongitude();
                                double init_lat = 10, init_long = 90;
                                if (distance(init_lat, init_long, latitude, longitude) < 100) {

                                    show_coordinates("Latitude: " + latitude + "\n" + "Longitude: " + longitude,latitude,longitude);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // proceed further
                                            Intent i = new Intent(geoActivity1.this, register.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }, 2000);


                                } else {
                                    display_distance_error();
                                }
                            }
                        }
                    }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getApplicationContext(), "GPS is already turned on", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(geoActivity1.this, 2);

                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    public void debug(String s) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Message :" + s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void show_coordinates(String s,double a,double b) {

        Toast.makeText(getApplicationContext(),s + "\ngreat you are at right place!",Toast.LENGTH_SHORT).show();


        // write the gps stored for further use ....

        String fileName = "myGps";
        try {
            FileOutputStream fOut = openFileOutput("myGPS", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(""+a+","+b);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            show_error("error while writing GPS Coordinates in external file , Error: " + e);
            e.printStackTrace();
        }
    }

    public void display_distance_error() {
        // person not in campus , automatically exit ....

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sorry , You are not in the specific location ! ");
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void show_error(String s) {

        // error due to file writing and other operations

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public double distance(double a, double b, double c, double d) {
        double dis = sqrt((a - c) * (a - c) + (b - d) * (b - d));
        double conversion_factor = 1;                   // to convert latitude dist to meter distsnace
        return dis * (conversion_factor);
    }
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(geoActivity1.this, LoginActivity.class);
        startActivity(i);
    }
}