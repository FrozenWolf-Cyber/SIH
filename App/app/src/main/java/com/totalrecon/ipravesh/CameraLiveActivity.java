package com.totalrecon.ipravesh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.LocationRequest;

import androidx.camera.camera2.internal.compat.quirk.AspectRatioLegacyApi21Quirk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.app.Activity;
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
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;
import com.totalrecon.ipravesh.cameraActivity;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.totalrecon.ipravesh.data.model.model;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraLiveActivity extends AppCompatActivity {

    // Define the pic id
    private static final int pic_id = 121;
    public boolean turned_left = true;
    private static boolean pic_taken = false;
    public model my_model;
    public float current_emebeds[] = new float[]{1,2,3};
    private LocationRequest locationRequest;

    Gson gson = new Gson();

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
    FaceDetector detector;

    public int n_opens = 0;
    public int n_closes = 0;
    public boolean closed = false;
    public boolean done = false;
    public boolean start_action = false;
    public boolean timer_activated = false;
    public boolean half_blink = false;
    public Bitmap main_img = null;

    int turns = Constant.turns_facedetection;
    int blinks = Constant.blinks_facedetection;
    int ACTION_TIME_ELAPSED =  Constant.action_time_facedetection;
    int time = ACTION_TIME_ELAPSED;
    String[] turn_actions_possible = {"LEFT", "RIGHT"};
    String chosen_action = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    TextView commands, timer;
    Button camera_switch;
    CameraSelector cameraSelector;
    boolean developerMode=false;
    boolean start=true,flipX=false;
    Context context=CameraLiveActivity.this;
    int cam_face=CameraSelector.LENS_FACING_BACK; //Default Back Camera

    CountDownTimer TIMER;

    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 1;

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public CountDownTimer generateTimer(){

        return new CountDownTimer(ACTION_TIME_ELAPSED, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("0:"+checkDigit(time));
                time--;
            }

            public void onFinish() {
                timer.setText(Constant.timer_tryagain_msg);
                time = ACTION_TIME_ELAPSED;


                show_alert(Constant.time_exceeded_msg);
//                Intent j = new Intent(CameraLiveActivity.this, check_status.class);
//                startActivity(j);
//                finish();
            }

        }.start();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_live);
        previewView =findViewById(R.id.previewView);
        camera_switch=findViewById(R.id.button3);
        commands = findViewById(R.id.commands);
        timer = findViewById(R.id.timer);




        my_model = new model(Constant.model_name, CameraLiveActivity.this);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        //On-screen switch to toggle between Cameras.
        camera_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cam_face==CameraSelector.LENS_FACING_BACK) {
                    cam_face = CameraSelector.LENS_FACING_FRONT;
                    flipX=true;
                }
                else {
                    cam_face = CameraSelector.LENS_FACING_BACK;
                    flipX=false;
                }
                cameraProvider.unbindAll();
                cameraBind();
            }
        });




        //Initialize Face Detector
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        cameraBind();

        TIMER = new CountDownTimer(ACTION_TIME_ELAPSED, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("0:"+checkDigit(time));
                time--;
            }

            public void onFinish() {
                timer.setText(Constant.timer_tryagain_msg);
                time = ACTION_TIME_ELAPSED;
                start_action = true;


            }

        }.start();

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



    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //Bind camera and preview view
    private void cameraBind()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView=findViewById(R.id.previewView);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cam_face)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(Constant.facedetection_img_width, Constant.facedetection_img_height))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                try {
                    Thread.sleep(0);  //Camera preview refreshed every 10 millisec(adjust as required)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                InputImage image = null;


                @SuppressLint("UnsafeExperimentalUsageError")
                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)

                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
                }

//                System.out.println("ANALYSIS");







                //Process acquired image to detect faces
                Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {
                                                float rightEyeOpenProb = -1;

                                                if (start_action) {
                                                    if (timer_activated == false){
                                                        TIMER = generateTimer();
                                                        if (blinks == 0 && turns == 0){
                                                            TIMER.cancel();
                                                            timer_activated = true;
                                                            do_verification();

                                                        }
                                                        if (blinks<turns){
                                                            turns--;
                                                            Random random = new Random();
                                                            chosen_action = turn_actions_possible[random.nextInt(turn_actions_possible.length)];
                                                            commands.setText("TURN "+chosen_action);
                                                            timer_activated = true;
                                                            Log.i("TURN","");

                                                        }
                                                        else{
                                                            blinks--;
                                                            chosen_action = "BLINK";
                                                            commands.setText("BLINK TWICE");
                                                            timer_activated = true;
                                                            Log.i("BLINK","");
                                                        }
                                                    }
                                                    if (faces.size() != 0) {

                                                        Face face = faces.get(0);

                                                        if (chosen_action == "BLINK") {
                                                            if (face.getRightEyeOpenProbability() != null) {
                                                                rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                                if (rightEyeOpenProb > Constant.right_eye_probability_true
                                                                        && closed == false) {
                                                                    n_opens += 1;
                                                                    if (main_img == null) {
                                                                        Bitmap frame_bmp = toBitmap(mediaImage);
                                                                        int rot = imageProxy.getImageInfo().getRotationDegrees();

                                                                        //Adjust orientation of Face
                                                                        main_img = rotateBitmap(frame_bmp, rot, false, false);
                                                                    }
                                                                    closed = true;

                                                                    if (half_blink == false){
                                                                        half_blink = true;
                                                                    }
                                                                    else{
                                                                        half_blink = false;
                                                                        closed = false;
                                                                        TIMER.cancel();
                                                                        timer_activated = false;
                                                                        time = ACTION_TIME_ELAPSED;
                                                                    }
                                                                } else if (rightEyeOpenProb < Constant.right_eye_probability_false && closed == true) {
                                                                    closed = false;
                                                                    n_closes += 1;
                                                                }
                                                            }
                                                        }


                                                        else if (chosen_action == "LEFT") {
                                                            if (face.getHeadEulerAngleY() >= Constant.head_angle_y) {
                                                                Log.i("U HAVE TURNED LEFT", "");
                                                                TIMER.cancel();
                                                                time = ACTION_TIME_ELAPSED;
                                                                timer_activated = false;

                                                            }
                                                        }

                                                        else if (chosen_action == "RIGHT") {
                                                            if (face.getHeadEulerAngleY() <= -Constant.head_angle_y) {
                                                                Log.i("U HAVE TURNED RIGHT", "");
                                                                TIMER.cancel();
                                                                time = ACTION_TIME_ELAPSED;
                                                                timer_activated = false;
                                                            }

                                                        }
                                                        // If face tracking was enabled:
                                                        if (face.getTrackingId() != null) {
                                                            int id = face.getTrackingId();
                                                        }


                                                        Log.i("X : ", Float.toString(face.getHeadEulerAngleX()));
                                                        Log.i("Y : ", Float.toString(face.getHeadEulerAngleY()));
                                                        Log.i("Z : ", Float.toString(face.getHeadEulerAngleZ()));

//                                                        if (n_closes >= 2 && done == false) {
//                                                            do_verification();
//                                                            done = true;
//                                                        }


                                                    } else {
                                                    }

                                                }

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        })
                                .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Face>> task) {

                                        imageProxy.close(); //v.important to acquire next frame for analysis
                                    }
                                });


            }
        });


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);

    }


    public void do_verification(){
        my_model.getEmbeddings((main_img));

        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.activity = CameraLiveActivity.this;
        loadingDialog.startLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                current_emebeds = my_model.embeds;
                Log.i("EMBEDS", Arrays.toString(current_emebeds));
                String verify = verified(current_emebeds);
                if (verify.equals("true")) {
                    Log.i("VERFICATION ", "YAAY VERFIED!!");
                    // verification
                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(Constant.location_setinterval);
                    locationRequest.setFastestInterval(Constant.location_setfastestinterval);
                    locationRequest.setSmallestDisplacement(Constant.location_setsmallestdisplacement);
                    getCurrentLocation(getApplicationContext());
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

        if (my_model.embeds.length == 1) {
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
    public String read_data(String filename)
    {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString(filename, "");
        return s1;
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
//                    show_alert("Your attendance has been recorded!");
                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(CameraLiveActivity.this);
                    alertDialogBuilder.setMessage(Constant.attendance_recorded_msg)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(CameraLiveActivity.this, check_status.class);
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


    public void show_alert(String s)
    {
        // Showing alert box
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CameraLiveActivity.this);
        alertDialogBuilder.setMessage(s)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
                        Intent j = new Intent(CameraLiveActivity.this, check_status.class);
                        startActivity(j);
                        finish();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Intent j = new Intent(CameraLiveActivity.this, check_status.class);
                        startActivity(j);
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

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
                                resolvableApiException.startResolutionForResult(CameraLiveActivity.this, 2);

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
    public void write_data(String filename,String data)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(filename, data);
        myEdit.commit();
    }
    public void display_distance_error() {
        // person not in campus, automatically exit ....

        show_message(Constant.not_in_office_msg);

        // go to checkin/checkout page when out of location

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(CameraLiveActivity.this, check_status.class);
                startActivity(i);
                finish();
            }
        }, Constant.delay_time);

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
        double zero_error = Constant.location_zero_error;
        if (dis < Constant.location_radius + zero_error) {
            write_data("latitude" , Double.toString(latitude));
            write_data("longitude" , Double.toString(longitude));
            send_log(Double.toString(latitude), Double.toString(longitude));
            // send the log status with location to server
        } else {
            display_distance_error();
        }

    }




    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);


        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), Constant.compressed_image_quality, out);

        byte[] imageBytes = out.toByteArray();
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    //Save Fac
    //Load Photo from phone storage
    private void loadphoto()
    {
        start=false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    //Similar Analyzing Procedure
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                    detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {

                            if(faces.size()!=0) {

                                Face face = faces.get(0);
//                                System.out.println(face);

                                //write code to recreate bitmap from source
                                //Write code to show bitmap to canvas

                                Bitmap frame_bmp= null;
                                try {
                                    frame_bmp = getBitmapFromUri(selectedImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                                //face_preview.setImageBitmap(frame_bmp1);


//                                System.out.println(boundingBox);
                                try {
                                    Thread.sleep(Constant.thread_sleep_time);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            start=true;
                            Toast.makeText(context, Constant.failed_toadd_msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void show_message(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}