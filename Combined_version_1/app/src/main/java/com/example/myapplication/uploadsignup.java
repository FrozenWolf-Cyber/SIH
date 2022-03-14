package com.example.myapplication;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.geoActivity;
import com.example.myapplication.geoActivity1;
import com.example.myapplication.register;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.ui.login.LoginViewModel;
import com.example.myapplication.ui.login.LoginViewModelFactory;
import com.example.myapplication.databinding.ActivityLoginBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

public class uploadsignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_final_upload);
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // upload data to server when signup ...

                String gps = read_local_storage("myGPS");
                String name ,desig , emplno , gender , office_add , phone , pass;
                name = read_local_storage("name");
                desig = read_local_storage("desig");
                emplno = read_local_storage("emplno");
                gender = read_local_storage("gender");
                office_add = read_local_storage("office_add");
                phone= read_local_storage("phone");
                pass = read_local_storage("pass");

                String new_user = read_local_storage("New_User");
                write_data(new_user+" "+pass);

                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();
                Bitmap photo1 = null;
                // retrive the image stored in the fileOutputStream
                try {
                    photo1 = BitmapFactory.decodeStream(openFileInput("myImage_1"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }Bitmap photo2 = null;
                // retrive the image stored in the fileOutputStream
                try {
                    photo1 = BitmapFactory.decodeStream(openFileInput("myImage_2"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }Bitmap photo3 = null;

                // retrive the image stored in the fileOutputStream
                try {
                    photo1 = BitmapFactory.decodeStream(openFileInput("myImage_3"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        });
    }
    public void write_data(String s) {
        String y = show_data();
        try {
            String fileName = "user_pass.txt";
            File f = new File(getApplicationContext().getFilesDir().getPath()+ File.separator + fileName);

            FileWriter lfilewriter = new FileWriter(f);
            BufferedWriter lout = new BufferedWriter(lfilewriter);
            lout.write(y+s+"\n");
            lout.close();
        } catch (IOException e) {
            show_error("write to sd card : "+e);
        }
    }
    public String show_data(){
        // Accessing the saved data from the downloads folder
        try {
            String fileName = "user_pass.txt";
            BufferedReader in = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), fileName)));
            String line = "";

            String data = "";
            while ((line = in.readLine()) != null)
                data += line;

            if (data != null) {
                return data;
            } else {
                show_error("No Data Found");
            }
        }
        catch(Exception e){
            show_error("error "+e);
        }
        return "";
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
    }@Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        Intent i = new Intent(uploadsignup.this, LoginActivity.class);
        startActivity(i);
    }
}