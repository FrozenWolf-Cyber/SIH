package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class threeshot extends AppCompatActivity {

    private static final int pic_id = 123;
    public int count_of_times[] = {0,0,0};
    private static boolean pic_taken = false;
    private ImageView imageView5 , imageView6 , imageView7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threeshot);
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
                    osw.flush();
                    osw.close();

                } catch (IOException e) {
                    show_error("error1 "+e);
                    e.printStackTrace();
                }

                Intent camera_intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);

            }

        });
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fOut = openFileOutput("cur_image",MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write("myImage_2");
                    osw.flush();
                    osw.close();

                } catch (IOException e) {
                    show_error("error1 "+e);
                    e.printStackTrace();
                }

                Intent camera_intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fOut = openFileOutput("cur_image",MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write("myImage_3");
                    osw.flush();
                    osw.close();

                } catch (IOException e) {
                    show_error("error1 "+e);
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
        if (requestCode == pic_id) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String fileName = "";//no .png or .jpg needed
            try {
                FileInputStream fin = openFileInput("cur_image");
                int c;
                String file_name = "";
                while ((c = fin.read()) != -1) {
                    file_name = file_name + Character.toString((char) c);
                }
                fin.close();
                if (file_name.equals("myImage_1")) {
                    imageView5.setBackgroundResource(0);
                    imageView5.setImageBitmap(photo);
                    createImageFromBitmap(photo,file_name);
                    count_of_times[0] += 1;
                }
                if (file_name.equals("myImage_2")) {
                    imageView6.setBackgroundResource(0);
                    imageView6.setImageBitmap(photo);
                    createImageFromBitmap(photo,file_name);
                    count_of_times[1] += 1;
                }
                if (file_name.equals("myImage_3")) {
                    imageView7.setBackgroundResource(0);
                    imageView7.setImageBitmap(photo);
                    createImageFromBitmap(photo,file_name);
                    count_of_times[2] += 1;
                }

                if (count_of_times[0] > 0 && count_of_times[1] > 0 && count_of_times[2] > 0) {

                    // all 3 photos have been taken ...

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            show_message("great , you can proceed to next step!");

                        }
                    }, 1000);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(threeshot.this, uploadsignup.class);
                            startActivity(i);
                            finish();
                        }
                    }, 1000);

                }
            } catch (Exception e) {
                show_error("error " + e);
            }

        }
    }

    public void show_error(String s) {

        // error due to file writing and other operations

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void show_message(String s) {

        // error due to file writing and other operations
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
    }@Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        Intent i = new Intent(threeshot.this, LoginActivity.class);
        startActivity(i);
    }
}