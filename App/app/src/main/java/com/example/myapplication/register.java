package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.ui.login.LoginActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class register extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Button button;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private EditText editText1;
    private TextView editText2;
    private TextView editText3;
    private TextView editText4;
    private TextView editText5;
    private TextView editText6;
    private TextView editText7;
    private Spinner spinner;
    private static final String[] paths = {"Male", "Female", "Other"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button=findViewById(R.id.button);
        textView=findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);
        textView4=findViewById(R.id.textView4);
        textView5=findViewById(R.id.textView5);
        textView6=findViewById(R.id.textView6);
        textView7=findViewById(R.id.textView7);
        editText1=findViewById(R.id.editText1);
        editText2=findViewById(R.id.editText2);
        editText3=findViewById(R.id.editText3);
        editText4=findViewById(R.id.editText4);
        editText5=findViewById(R.id.editText5);
        editText6=findViewById(R.id.editText6);
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(register.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // operation when the button is clicked...

                String name ,desig , emplno , gender , office_add , phone , pass;
                name = editText1.getText().toString();
                desig = editText2.getText().toString();
                emplno = editText3.getText().toString();
                gender = spinner.getSelectedItem().toString();
                office_add = editText4.getText().toString();
                phone = editText5.getText().toString();
                pass = editText6.getText().toString();

                String attributes[] = {"name" , "designation" , "employee number" , "gender" , "office address" , "phone number" , "password "};
                String values[] = {name , desig , emplno , gender , office_add , phone , pass};


                int flag = 0;
                for(int i = 0 ; i < 7 ; i ++){
                    if (values[i].replace(" ","").equals("")){
                        // check if empty
                        flag = 1;
                        show_error("Please fill up the "+attributes[i]+" in appropriate format !");
                        break;
                    }
                }
                if(flag == 0){
                    // all values are entered correctly , proceed further !!!

                    write_data_local("name", name);
                    write_data_local("desig", desig);
                    write_data_local("emplno", emplno);
                    write_data_local("gender", gender);
                    write_data_local("office_add", office_add);
                    write_data_local("phone", phone);
                    write_data_local("pass", pass);

                    Intent i = new Intent(register.this, threeshot.class);
                    startActivity(i);
                    finish();
                }

            }
        });
    }
    public void write_data_local(String filename , String val){
        try {
            FileOutputStream fOut = openFileOutput(filename,MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(val);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            show_error("error1 "+e);
            e.printStackTrace();
        }
    }
    public void show_error(String s) {

        // error due to file writing and other operations

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
        Intent i = new Intent(register.this, LoginActivity.class);
        startActivity(i);
    }
}