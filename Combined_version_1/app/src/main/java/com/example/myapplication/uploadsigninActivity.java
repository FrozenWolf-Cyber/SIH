package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.ui.login.LoginActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class uploadsigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_sign_in_activity);
        Bitmap photo = null;

        // retrive the image stored in the fileOutputStream
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button button = (Button)findViewById(R.id.button_second);
        try {
            photo = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            imageView.setImageBitmap(photo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // alert dialogue to be given if fails ...
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String cur_user = read_local_storage("Cur_User");
                String gps = read_local_storage("myGPS");
                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();

                // write data in log file ...
                write_data(cur_user+"@"+gps+"@"+time+"\n");
                Bitmap photo1 = null;

                // retrive the image stored in the fileOutputStream
                try {
                    photo1 = BitmapFactory.decodeStream(openFileInput("myImage"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //make a post request ....


                show_message("Logfile: "+ show_data());

            }
        });


    }
    public String read_local_storage(String filename)
    {
        try {
            // read the myGPS file ....
            FileInputStream fin = openFileInput(filename);
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            fin.close();
            if (temp != "") {           // if myGPS file not empty , proceed to next operation
                return temp;
            } else {
                show_error("file is empty");
            }
        }catch(Exception e){

            show_error("error "+e);
        }
        return "";
    }
    public void show_error(String s) {

        // error due to file writing and other operations

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void write_data(String s) {
        String y = show_data();
        try {
            String fileName = "log.txt";
            File f = new File(getApplicationContext().getFilesDir().getPath()+ File.separator + fileName);

            FileWriter lfilewriter = new FileWriter(f);
            BufferedWriter lout = new BufferedWriter(lfilewriter);
            lout.write(y+s+"\n");
            lout.close();
        } catch (IOException e) {
            show_message("write to sd card : "+e);
        }
    }
    public String show_data(){
        // Accessing the saved data from the downloads folder
        try {


            String fileName = "log.txt";
            BufferedReader in = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), fileName)));
            String line = "";

            String data = "";
            while ((line = in.readLine()) != null)
                data += line;

            if (data != null) {
                return data;
            } else {
                show_message("No Data Found");
            }
        }
        catch(Exception e){
            show_message("error "+e);
        }
        return "";
    }
    public void show_message(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        Intent i = new Intent(uploadsigninActivity.this, LoginActivity.class);
        startActivity(i);
    }
}