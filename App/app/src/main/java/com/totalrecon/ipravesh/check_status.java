package com.totalrecon.ipravesh;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.totalrecon.ipravesh.ui.login.LoginActivity;

public class check_status extends AppCompatActivity{
    private Button check_both , exit_button , dashboard;
    private TextView textView;
    private EditText editText1;

    String cur_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_status_layout);
        check_both=(Button)findViewById(R.id.buttonKavi);
        dashboard=(Button)findViewById(R.id.buttonKavi2);
        exit_button=(Button)findViewById(R.id.button2);
        
        editText1 = (EditText) findViewById(R.id.password);

        cur_status = read_data("check_status");
//        show_message("debugging purpose: cur_status = "+cur_status);
//        if (cur_status.equals("checkin")) {
//            show_message("You will be checking out now!");
//        }
//        else {
//            show_message("You will be checking in now!");
//        }
//        show_message("debug:  " + cur_status);
        check_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (cur_status.equals("")) {
                        Log.i("inside","inside");
                        write_data("check_status", "checkout");
                        Log.i("After change", read_data("check_status"));
                        cur_status = read_data("check_status");
                    }
                    if (cur_status.equals("checkout")) {

                        // proceed ...
                        show_message("Recording your entry attendance now!");
                        Intent i = new Intent(check_status.this,cameraActivity.class);
                        startActivity(i);

                    }else{
                        // proceed ...
                        show_message("Recording your exit attendance now!");
                        Intent i = new Intent(check_status.this, cameraActivity.class);
                        startActivity(i);
                    }

            }
        });
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_message("Logged out successfully!");
                Intent i = new Intent(check_status.this, LoginActivity.class);
                startActivity(i);
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_message("Dashboard opened");
                Intent i = new Intent(check_status.this, employee_dashboard.class);
                startActivity(i);
            }
        });
    }
    public void show_message(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
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
