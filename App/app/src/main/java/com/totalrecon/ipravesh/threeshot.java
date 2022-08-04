package com.totalrecon.ipravesh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.*;
import java.io.*;

import com.totalrecon.ipravesh.R;

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

    // Class for storing embeds with user_id
    public class idWithEmbeds {
        public String user_id = "dummy";
        public float[] embed1 = {};
        public float[] embed2 = {};
        public float[] embed3 = {};
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

    idWithEmbeds user_embeds = new idWithEmbeds();
    public model my_model;
    private String upload_URL = "https://sih-smart-attendance.herokuapp.com/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threeshot);
        my_model = new model("inference_model_993_quant.tflite", threeshot.this);
        imageView5 = (ImageView)findViewById(R.id.imageView5);
        imageView6 = (ImageView)findViewById(R.id.imageView6);
        imageView7 = (ImageView)findViewById(R.id.imageView7);
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
                            Intent i = new Intent(threeshot.this, uploadsignup.class);
                            startActivity(i);
                            finish();
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
                            Intent i = new Intent(threeshot.this, uploadsignup.class);
                            startActivity(i);
                            finish();
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
                            Intent i = new Intent(threeshot.this, uploadsignup.class);
                            startActivity(i);
                            finish();
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
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        Intent i = new Intent(threeshot.this, LoginActivity.class);
        startActivity(i);
    }
}