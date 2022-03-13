package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.ui.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class cameraActivity extends AppCompatActivity {

    // Define the pic id
    private static final int pic_id = 123;
    private static boolean pic_taken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button buttonFirst = (Button)findViewById(R.id.button_first);

        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // CAMERA FUNCTIONALITY HERE :
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

            // BitMap is data structure of image file
            // which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            createImageFromBitmap(photo);       //store in fileoutputstream memory such that it can be reused by the second activity


            // go to the second activity ...
            Intent i = new Intent(cameraActivity.this, uploadsigninActivity.class);
            startActivity(i);
            finish();
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

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
        Intent i = new Intent(cameraActivity.this, LoginActivity.class);
        startActivity(i);
    }
}