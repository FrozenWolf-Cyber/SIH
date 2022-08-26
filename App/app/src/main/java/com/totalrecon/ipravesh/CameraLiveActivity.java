package com.totalrecon.ipravesh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.compat.quirk.AspectRatioLegacyApi21Quirk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

    int turns = 2;
    int blinks = 3;
    int ACTION_TIME_ELAPSED = 6000;
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
    private static final int MY_CAMERA_REQUEST_CODE = 100;

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
                timer.setText("try again");
                time = ACTION_TIME_ELAPSED;
                Intent j = new Intent(CameraLiveActivity.this, check_status.class);
                startActivity(j);
                finish();
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


        my_model = new model("mobile_face_net.tflite", CameraLiveActivity.this);
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
                timer.setText("try again");
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
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
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
                        .setTargetResolution(new Size(640, 480))
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
//                                                if (faces.size()>1){
//                                                    show_message("MORE THAN ONE FACE IN THE FRAME");
//                                                    Intent i = new Intent(CameraLiveActivity.this, check_status.class);
//                                                    startActivity(i);
//                                                    finish();
//                                                }

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
                                                                if (rightEyeOpenProb > 0.9 && closed == false) {
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
                                                                } else if (rightEyeOpenProb < 0.1 && closed == true) {
                                                                    closed = false;
                                                                    n_closes += 1;
                                                                }
                                                            }
                                                        }


                                                        else if (chosen_action == "LEFT") {
                                                            if (face.getHeadEulerAngleY() >= 30) {
                                                                Log.i("U HAVE TURNED LEFT", "");
                                                                TIMER.cancel();
                                                                time = ACTION_TIME_ELAPSED;
                                                                timer_activated = false;

                                                            }
                                                        }

                                                        else if (chosen_action == "RIGHT") {
                                                            if (face.getHeadEulerAngleY() <= -30) {
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
//                    show_alert("Verified!");
                    loadingDialog.dismissDialog();
                    Intent i = new Intent(CameraLiveActivity.this, geoActivity.class);
                    startActivity(i);
                    finish();

//                    Toast.makeText(getApplicationContext(), "VERIFIED !!", Toast.LENGTH_SHORT).show();
//                    inverse_check_in_out();
                }
                if (verify.equals("false")) {
                    loadingDialog.dismissDialog();
                    show_alert("Face doesn't match!");
                }
                if (verify.equals("many faces")) {
                    loadingDialog.dismissDialog();
                    show_alert("There are many faces!");
                }
                if (verify.equals("no face")) {
                    loadingDialog.dismissDialog();
                    show_alert("There are no faces!");
//                    Toast.makeText(getApplicationContext(), "FACE DOESN'T MATCH", Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);

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
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

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
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            start=true;
                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
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