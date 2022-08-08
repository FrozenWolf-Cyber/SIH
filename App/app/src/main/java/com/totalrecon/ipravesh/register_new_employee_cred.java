package com.totalrecon.ipravesh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.totalrecon.ipravesh.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class register_new_employee_cred extends AppCompatActivity{

    private Button button;
    private EditText username , password , confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_newemployee_cred);

        button=findViewById(R.id.next);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        confirmpassword=findViewById(R.id.confirmpassword);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // operation when the button is clicked...
                String user_name , pass_word , confirm_password;
                user_name = username.getText().toString();
                pass_word = password.getText().toString();
                confirm_password = confirmpassword.getText().toString();
                // verify details

                int flag = 0;

                // some validation , input should never be empty

                if(user_name.replace(" ","").equals("") ||
                        pass_word.replace(" ","").equals("")) {
                    show_error("Please fill up appropriate username and password");
                    flag = 1;
                }
                else if(!(pass_word.equals(confirm_password)))
                {
                    show_error("Confirm password does not match with the given password");
                    flag = 1;
                }

                if(flag == 0){
                    // post request verify ...

                    write_data("username", user_name);
                    write_data("password", pass_word);

                    Intent i = new Intent(register_new_employee_cred.this, threeshot.class);
                    startActivity(i);
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
}