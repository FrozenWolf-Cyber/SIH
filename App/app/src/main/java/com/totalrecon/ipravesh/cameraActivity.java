package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.compat.quirk.AspectRatioLegacyApi21Quirk;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
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
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;

import com.google.gson.Gson;

import com.totalrecon.ipravesh.data.model.model;
import com.totalrecon.ipravesh.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class cameraActivity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 1;

    // Define the pic id
    private static final int pic_id = 121;
    public boolean flag = false;
    private static boolean pic_taken = false;
    private LocationRequest locationRequest;

    public model my_model;
    public float current_emebeds[] = new float[]{1,2,3};
    Gson gson = new Gson();

    LoadingDialog loadingDialog = new LoadingDialog();

    public class logDetails {
        public String emp_no;
        public String check_in;
        public String check_out;
    }

    public logDetails log_obj = new logDetails();

    public class idWithEmbeds {
        public String emp_no;
        public float[] embed1;
        public float[] embed2;
        public float[] embed3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);

        my_model = new model(Constant.model_name, cameraActivity.this);
        loadingDialog.activity = cameraActivity.this;
        // Default camera functionality start
        start_camera();
    }

    public void start_camera()
    {
        // CAMERA FUNCTIONALITY HERE :
        try {
            FileOutputStream fOut = openFileOutput("cur_image",MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write("verify_img");
            osw.flush(); osw.close();
        } catch (IOException e) {
//                    show_error("error1 "+e);
            e.printStackTrace();
        }
        Intent camera_intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_intent.putExtra("android.intent.extras.CAMERA_FACING", 1); // Open front camera first
        startActivityForResult(camera_intent, pic_id);
    }

    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        // Match the request 'pic id with requestCode
        super.onActivityResult(requestCode, resultCode, data);

            // BitMap is data structure of image file
            // which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");//store in fileoutputstream memory such that it can be reused by the second activity
//        Bitmap t = null;
//        if (photo == t){
//            Log.i("BITMAP","NULL");
//        }
            Log.i("BITMAP", photo.toString());
            my_model.getEmbeddings((photo));

            loadingDialog.startLoadingDialog();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    current_emebeds = my_model.embeds;
                    Log.i("EMBEDS", Arrays.toString(current_emebeds));
                    String verify = verified(current_emebeds);

                    if (verify.equals("true")) {
                        Log.i("RESPONSE", "YAAY VERFIED!!");

                        // check geolocation

                        locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(Constant.location_setinterval);
                        locationRequest.setFastestInterval(Constant.location_setfastestinterval);
                        locationRequest.setSmallestDisplacement(Constant.location_setsmallestdisplacement);
                        getCurrentLocation(getApplicationContext());

                        // verification
//                    show_alert("Verified!");
//                        send_log(read_data("latitude") , read_data("longitude"));

//                    Toast.makeText(getApplicationContext(), "VERIFIED !!", Toast.LENGTH_SHORT).show();
//                    inverse_check_in_out();
                    }
                    if (verify.equals("false")) {
                        loadingDialog.dismissDialog();
                        show_alert(Constant.face_not_match_msg);
                    }
                    if (verify.equals("many faces")) {
                        loadingDialog.dismissDialog();
                        show_alert(Constant.face_many_msg);
                    }
                    if (verify.equals("no face")) {
                        loadingDialog.dismissDialog();
                        show_alert(Constant.face_no_msg);
//                    Toast.makeText(getApplicationContext(), "FACE DOESN'T MATCH", Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constant.model_delay_time);


    }


    public String verified(float[] user_embeds){
        float[] embeds_n;
        float  distance;

        if (my_model.embeds != null && my_model.embeds.length == 1) {
            return "many faces";
        }

        SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
        String s = sh.getString("json", "");
        idWithEmbeds obj = gson.fromJson(s, idWithEmbeds.class);

        log_obj.emp_no = obj.emp_no;

        try {
            embeds_n = obj.embed1;
            distance = my_model.findDistance(embeds_n, user_embeds);
            if (distance<1.000f){
                return "true";
            }

            embeds_n = obj.embed2;
            distance = my_model.findDistance(embeds_n, user_embeds);
            if (distance<1.000f){
                return "true";
            }

            embeds_n = obj.embed3;
            distance = my_model.findDistance(embeds_n, user_embeds);
            if (distance<1.000f){
                return "true";
            }

            return "false";
        } catch (Exception e) {
            return "no face";
        }

    }


    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
    public String read_data(String filename)
    {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString(filename, "");
        return s1;
    }
    public void write_data(String filename,String data)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(filename, data);
        myEdit.commit();
    }

    // same functions as in geoActivity.java

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    public void getCurrentLocation(Context context ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
                                Log.i("Latitude", "" + Double.toString(latitude));
                                Log.i("Longitude", "" + Double.toString(longitude));
                                check_distance(latitude, longitude);

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
                    Toast.makeText(getApplicationContext(), Constant.gps_turned_on_msg, Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(cameraActivity.this, 2);

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

    public void check_distance(double latitude, double longitude ) {

        double[] coord = new double[2];

        String emp_no = read_data("emp_no");
        String branch_name = read_data("branch_name");
        coord[0] = Double.parseDouble(read_data("latitude"));
        coord[1] = Double.parseDouble(read_data("longitude"));
                        // calculate distance
        double dis = distance(coord[0], coord[1], latitude, longitude);
        Log.i("Office", coord[0] + " " + coord[1]);
        Log.i("RESPONSE", dis + "\n" + latitude + "\n" + longitude);

        // dis is in km
        double zero_error = 30 * (0.1);
        if (dis < 0.1 + zero_error) {
            write_data("latitude" , Double.toString(latitude));
            write_data("longitude" , Double.toString(longitude));
            send_log(Double.toString(latitude), Double.toString(longitude));

            loadingDialog.dismissDialog();
            // send the log status with location to server
         } else {
            display_distance_error();
         }

    }

    public void show_alert(String s)
    {
        // Showing alert box
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cameraActivity.this);
        alertDialogBuilder.setMessage(s)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
                Intent j = new Intent(cameraActivity.this, check_status.class);
                startActivity(j);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Intent j = new Intent(cameraActivity.this, check_status.class);
                        startActivity(j);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void display_distance_error() {
        // person not in campus, automatically exit ....

        show_message(Constant.not_in_office_msg);

        // go to checkin/checkout page when out of location

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(cameraActivity.this, check_status.class);
                startActivity(i);
                finish();
            }
        }, Constant.delay_time);

    }

    public void show_message(String s) {
        try {
            // error due to file writing and other operations
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(s);
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e("AlertBoxError", e + "");
        }
    }
    public double distance(double lat1, double lon1, double lat2, double lon2) {

        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = Constant.radius_of_earth;        // radius of earth
        return (c * r);          // in km

    }
    public void send_log(String latitude , String longitude) {

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy@hh:mm:ss");
        String strDate = dateFormat.format(date);
//        String strDate = "debug time test";

//        log_obj.check_in = "";
//        log_obj.check_out = "";

        String cur_status = read_data("check_status");
        if (cur_status.equals("\"CHECKED OUT\"")) {
            log_obj.check_in = strDate;
            log_obj.check_out = "blah-null";
        } else {
            log_obj.check_out = strDate;
            log_obj.check_in = "blah-null";
        }
        log_obj.emp_no = read_data("emp_no");

        Log.i("EMP NO ", log_obj.emp_no);
        Log.i(log_obj.check_in, log_obj.check_out);
/*
    post request :  /update_log
    parameters :
        emp_no
        check_in
        check_out
        latitude
        longitude
    response:
        attendance recorded message , goto check_status
*/
        String upload_URL = Constant.update_log_url;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    inverse_check_in_out();
//                    show_alert("Your attendance has been recorded!");
                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(cameraActivity.this);
                    alertDialogBuilder.setMessage(Constant.attendance_recorded_msg)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(cameraActivity.this, check_status.class);
                                    startActivity(i);
                                }
                            });
                    androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
//                    Toast.makeText(getApplicationContext(), "Your attendance has been recorded!", Toast.LENGTH_SHORT);


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emp_no", log_obj.emp_no);
                params.put("check_in", log_obj.check_in);
                params.put("check_out", log_obj.check_out);
                params.put("latitude", latitude);
                params.put("longitude",longitude);
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }
    public void inverse_check_in_out(){
        String cur_status = read_data("check_status");
        if (cur_status.equals("checkin")){
            write_data("check_status", "checkout");
        }
        else {
            write_data("check_status", "checkin");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, Constant.camera_permission_granted_msg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, Constant.camera_permission_denied_msg, Toast.LENGTH_LONG).show();
            }
        }
    }

}
