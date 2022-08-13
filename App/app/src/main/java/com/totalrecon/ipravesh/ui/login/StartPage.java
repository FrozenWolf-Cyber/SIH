package com.totalrecon.ipravesh.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.totalrecon.ipravesh.R;


import com.totalrecon.ipravesh.register_new_employee;

public class StartPage extends AppCompatActivity {

    public class idWithArray {
        public String user_name;
        public String password;
        public String name;
    }
    Button loginButton , signupButton;
    idWithArray obj = new idWithArray();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //clear_data();
        //show_message(show_data());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        loginButton = (Button) findViewById(R.id.backbutton);
        signupButton = (Button) findViewById(R.id.signup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartPage.this, LoginActivity.class);
                startActivity(i);

            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartPage.this, register_new_employee.class);
                startActivity(i);

            }
        });
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
    public boolean search(String s1 , String s2){
        return s2.contains(s1);
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
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
            finish();
    }

}