package com.totalrecon.ipravesh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.*;
import java.io.*;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.totalrecon.ipravesh.R;

import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;
import com.totalrecon.ipravesh.data.model.model;
import com.totalrecon.ipravesh.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class threeshot extends AppCompatActivity {

    private static final int pic_id1 = 121;
    private static final int pic_id2 = 122;
    private static final int pic_id3 = 123;
    public int count_of_times[] = {0,0,0};
    private static boolean pic_taken = false;
    private ImageView imageView5 , imageView6 , imageView7;
    private CheckBox checkbox;

    public boolean uploaded_state = true;
    /*
         By default: uploaded_state = true
         When "ALREADY IN USE" response is received, then uploaded_state = false
     */

    private String upload_URL = "https://sih-smart-attendance.herokuapp.com/signup";
    Bitmap headshot;

    // Class for storing embeds with user_id
    public class idWithEmbeds {
        public String emp_no = "dummy";
        public float[] embed1 = {};
        public float[] embed2 = {};
        public float[] embed3 = {};
    }
    public class idWithArray {

        public String user_name;
        public String password;
        public String emp_no;
    }

    // Saving embeddings after feature extraction
    public void saveEmbedsToSP(idWithEmbeds obj) {
        SharedPreferences sharedPreferences = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json_string = gson.toJson(obj);
        myEdit.putString("json", json_string);
        myEdit.commit();
    }

    idWithArray obj = new idWithArray();
    idWithEmbeds user_embeds = new idWithEmbeds();
    public model my_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threeshot);
        // declare all elements

        my_model = new model("mobile_face_net.tflite", threeshot.this);
        imageView5 = (ImageView)findViewById(R.id.imageView5);
        imageView6 = (ImageView)findViewById(R.id.imageView6);
        imageView7 = (ImageView)findViewById(R.id.imageView7);
        checkbox = (CheckBox) findViewById(R.id.checkBox);

        Button submit_button = findViewById(R.id.button5);
        Button back_button = findViewById(R.id.button6);
        imageView5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fOut = openFileOutput("cur_image",MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write("myImage_1");
                    osw.flush(); osw.close();
                } catch (IOException e) {
                    show_error("error1 "+e);
                    e.printStackTrace();
                }
                Intent camera_intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id1);
            }
        });
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fOut = openFileOutput("cur_image",MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write("myImage_2");
                    osw.flush(); osw.close();
                } catch (IOException e) {
                    show_error("error1 "+e);
                    e.printStackTrace();
                }
                Intent camera_intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id2);
            }
        });
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fOut = openFileOutput("cur_image",MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write("myImage_3");
                    osw.flush(); osw.close();
                } catch (IOException e) {
                    show_error("error1 "+e);
                    e.printStackTrace();
                }
                Intent camera_intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id3);
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            // upload all details to server
            @Override
            public void onClick(View view) {
                // if signup button clicked ,
                upload_sign_up();

            }
        });
        submit_button.setEnabled(false);
        back_button.setOnClickListener(new View.OnClickListener() {
            // back button
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(threeshot.this, register_new_employee_cred.class);
//                startActivity(i);
                  threeshot.super.onBackPressed();
            }
        });
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit_button.setEnabled(true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Match the request 'pic id' with requestCode
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id1) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            my_model.getEmbeddings((photo));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user_embeds.embed1 = my_model.embeds;
                    Log.i("EMBEDS", Arrays.toString(user_embeds.embed1));
                }
            }, 2000);

            String fileName = "";//no .png or .jpg needed
            try {
                FileInputStream fin = openFileInput("cur_image");
                int c;
                String file_name = "";
                while ((c = fin.read()) != -1) {
                    file_name = file_name + Character.toString((char) c);
                }
                fin.close();

                imageView5.setBackgroundResource(0);
                imageView5.setImageBitmap(photo);
                createImageFromBitmap(photo,file_name);
                count_of_times[0] += 1;

                if (count_of_times[0] > 0 && count_of_times[1] > 0 && count_of_times[2] > 0) {
                    // All 3 photos have been taken...
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveEmbedsToSP(user_embeds);
                            Log.i("EmbedsSPSave", "Saved to SharedPref");
                            show_message("Great, you can proceed to next step!");
                        }
                    }, 2000);
                    // Go to confirm page
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 2000);
                }
            } catch (Exception e) {
                show_error("error " + e);
            }
        }
        if (requestCode == pic_id2) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            my_model.getEmbeddings((photo));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user_embeds.embed2 = my_model.embeds;
                    Log.i("EMBEDS", Arrays.toString(user_embeds.embed2));
                }
            }, 2000);

            String fileName = "";//no .png or .jpg needed
            try {
                FileInputStream fin = openFileInput("cur_image");
                int c;
                String file_name = "";
                while ((c = fin.read()) != -1) {
                    file_name = file_name + Character.toString((char) c);
                }
                fin.close();

                imageView6.setBackgroundResource(0);
                imageView6.setImageBitmap(photo);
                createImageFromBitmap(photo,file_name);
                count_of_times[1] += 1;

                if (count_of_times[0] > 0 && count_of_times[1] > 0 && count_of_times[2] > 0) {
                    // All 3 photos have been taken ...
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveEmbedsToSP(user_embeds);
                            Log.i("SPSave", "Saved to SharedPref");
                            show_message("Great, you can proceed to next step!");
                        }
                    }, 2000);
                    // Go to confirm page
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 2000);
                }
            } catch (Exception e) {
                show_error("error " + e);
            }
        }
        if (requestCode == pic_id3) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            my_model.getEmbeddings((photo));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user_embeds.embed3 = my_model.embeds;
                    Log.i("EMBEDS", Arrays.toString(user_embeds.embed3));
                }
            }, 2000);

            String fileName = "";//no .png or .jpg needed
            try {
                FileInputStream fin = openFileInput("cur_image");
                int c;
                String file_name = "";
                while ((c = fin.read()) != -1) {
                    file_name = file_name + Character.toString((char) c);
                }
                fin.close();

                imageView7.setBackgroundResource(0);
                imageView7.setImageBitmap(photo);
                createImageFromBitmap(photo,file_name);
                count_of_times[2] += 1;

                if (count_of_times[0] > 0 && count_of_times[1] > 0 && count_of_times[2] > 0) {
                    // All 3 photos have been taken ...
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveEmbedsToSP(user_embeds);
                            Log.i("SPSave", "Saved to SharedPref");
                            show_message("Great, you can proceed to next step!");
                        }
                    }, 2000);
                    // Go to confirm page
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 2000);
                }
            } catch (Exception e) {
                show_error("error " + e);
            }
        }
    }

    public void show_error(String s) {
        // Error due to file writing and other operations
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void show_message(String s) {
        // Error due to file writing and other operations
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
    public String createImageFromBitmap(Bitmap bitmap,String fileName) {
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


    public void upload_sign_up()
    {
            readSP();
            SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
            String s = sh.getString("json", "");
            Gson gson = new Gson();
            idWithEmbeds obj2 = gson.fromJson(s, idWithEmbeds.class);
            user_embeds.embed1 = obj2.embed1;
            user_embeds.embed2 = obj2.embed2;
            user_embeds.embed3 = obj2.embed3;
//                Log.i("obj", obj.emp_no);
            // Retrieve the image stored in the fileOutputStream
            try {
                headshot = BitmapFactory.decodeStream(openFileInput("myImage_1"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Upload to server
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        user_embeds.emp_no = json_rec;
                        Log.i("Return from server: ", json_rec);
                        saveEmbedsToSP(user_embeds);
                        clearSP();
                        Intent i = new Intent(threeshot.this, LoginActivity.class);
                        startActivity(i);
                        finish();

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

                    params.put("user_name", obj.user_name);
                    params.put("password", obj.password);
                    params.put("emp_no", obj.emp_no);
                    params.put("embed1", Arrays.toString(user_embeds.embed1));
                    params.put("embed2", Arrays.toString(user_embeds.embed2));
                    params.put("embed3", Arrays.toString(user_embeds.embed3));
                    Log.i("params", params.toString());
                    return params;
                }

                // Comment below getByteData() function for no image transfer to server
                @Override
                protected Map<String, DataPart> getByteData() throws IOException {
                    Map<String, DataPart> params = new HashMap<>();
                    Bitmap bitmap = headshot;
                    //ivSource.setImageBitmap(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    params.put("files", new DataPart("file.jpg", stream.toByteArray(), "image/jpg"));
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);




    }

    public void readSP() {

        obj.user_name = read_data("username");
        obj.password = read_data("password");
        obj.emp_no = read_data("emplno");

    }

    public void clearSP() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
//        Intent i = new Intent(threeshot.this, register_new_employee_cred.class);
//        startActivity(i);
    }

    public String read_data(String filename) {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString(filename, "");
        return s1;
    }
}