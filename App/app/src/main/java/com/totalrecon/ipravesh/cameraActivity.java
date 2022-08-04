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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
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

    // Define the pic id
    private static final int pic_id = 121;
    private static boolean pic_taken = false;
    public model my_model;
    public float current_emebeds[] = new float[]{1,2,3};
    Gson gson = new Gson();

    public class idWithEmbeds {
        public String user_id;
        public float[] embed1;
        public float[] embed2;
        public float[] embed3;
    }
    public class logDetails {
        public String user_id;
        public String check_in;
        public String check_out;
    }

    public logDetails log_obj = new logDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button buttonFirst = (Button)findViewById(R.id.button_first);

        my_model = new model("mobilefacenet.tflite", cameraActivity.this);
        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                startActivityForResult(camera_intent, pic_id);
            }
        });
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
        Log.i("BITMAP",photo.toString());
        my_model.getEmbeddings((photo));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                current_emebeds = my_model.embeds;
                Log.i("EMBEDS", Arrays.toString(current_emebeds));
                String verify = verified(current_emebeds);
                if (verify.equals("true")){
                    Log.i("VERFICATION ","YAAY VERFIED!!");
                    // verification
//                    show_alert("Verified!");
                    send_log();
//                    Toast.makeText(getApplicationContext(), "VERIFIED !!", Toast.LENGTH_SHORT).show();
//                    inverse_check_in_out();
                }
                if (verify.equals("false")) {
                    show_alert("Face doesn't match!");
                }
                if (verify.equals("no face")) {
                    show_alert("There are no faces!");
//                    Toast.makeText(getApplicationContext(), "FACE DOESN'T MATCH", Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);

    }

    public void send_log(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy@hh:mm:ss");
        String strDate = dateFormat.format(date);
//        String strDate = "debug time test";

//        log_obj.check_in = "";
//        log_obj.check_out = "";

        String cur_status = read_data("check_status");
        if (cur_status.equals("checkout")){
            log_obj.check_in = strDate;
            log_obj.check_out = "blah-null";
        }
        else {
            log_obj.check_out = strDate;
            log_obj.check_in = "blah-null";
        }

        Log.i("USER ID ", log_obj.user_id);
        Log.i(log_obj.check_in, log_obj.check_out);

        String upload_URL = "https://sih-smart-attendance.herokuapp.com/update_log";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    inverse_check_in_out();
//                    show_alert("Your attendance has been recorded!");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cameraActivity.this);
                    alertDialogBuilder.setMessage("Your attendance is recorded!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(cameraActivity.this, check_status.class);
                            startActivity(i);
                        }
                    });
                    AlertDialog alert = alertDialogBuilder.create();
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
                params.put("user_id", log_obj.user_id);
                params.put("check_in", log_obj.check_in);
                params.put("check_out", log_obj.check_out);
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

// Send logs
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

    public String verified(float[] user_embeds){
        float[] embeds_n;
        float  distance;

        SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
        String s = sh.getString("json", "");
        idWithEmbeds obj = gson.fromJson(s, idWithEmbeds.class);

        log_obj.user_id = obj.user_id;
        try {
            embeds_n = obj.embed1;
            distance = my_model.findDistance(embeds_n, user_embeds);
            if (distance<48.703f){
                return "true";
            }

            embeds_n = obj.embed2;
            distance = my_model.findDistance(embeds_n, user_embeds);
            if (distance<48.703f){
                return "true";
            }

            embeds_n = obj.embed3;
            distance = my_model.findDistance(embeds_n, user_embeds);
            if (distance<48.703f){
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
    public void show_alert(String s)
    {
        // Showing alert box
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cameraActivity.this);
        alertDialogBuilder.setMessage(s)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
