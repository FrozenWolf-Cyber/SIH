package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.totalrecon.ipravesh.R;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

public class uploadsigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);


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


                String username = read_data("user_name");
                String gps = read_data("myGps");
                String cur_state = read_data("check_status");
                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();
                String array[] = read_array_data(username);
                display_array_data(array);
                Bitmap photo1 = null;

                // retrive the image stored in the fileOutputStream

                try {
                    photo1 = BitmapFactory.decodeStream(openFileInput("myImage"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // make a post request ....
                // code here below ...




                show_message("debugging purpose\n"+username+"@"+gps+"@"+cur_state+"@"+currentTime);
            }
        });


    }
    public void display_array_data(String[] s)
    {
        String s1 = "";
        String attr[]={"name" ,"username", "emplno" ,"desig" , "emailid" , "gender" , "office_add" , "phone" , "pass"};
        for(int i = 0 ; i<9 ;i ++)s1+=attr[i]+":  "+s[i]+"\n";
        Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_SHORT).show();
    }

    public String[] read_array_data(String username) {

        String array[]={"","","","","","","","",""};

        for(int i = 0 ; i<9 ;i ++){

            String filename = username+"."+"datasore"+"."+i;
            array[i] = read_data(filename).toString();
        }
        return array;
    }

    public void show_error(String s) {

        // error due to file writing and other operations

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void show_message(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Exit();
                finishAffinity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    public void Exit(){
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }
}