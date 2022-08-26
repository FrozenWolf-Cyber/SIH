package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;
import com.totalrecon.ipravesh.ui.login.LoginActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class check_status extends AppCompatActivity {
    private Button check_both, exit_button, dashboard;
    private TextView textView;
    private EditText editText1;
    CardView cardView;
    String cur_status;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dahsboard_home);
//        check_both=(Button)findViewById(R.id.buttonKavi);
//        dashboard=(Button)findViewById(R.id.buttonKavi2);
//        exit_button=(Button)findViewById(R.id.button2);
        cardView = findViewById(R.id.cardview);
//        editText1 = (EditText) findViewById(R.id.password);
        textView = findViewById(R.id.textView22);
        navView = findViewById(R.id.nav_view);

//        cur_status = read_data("check_status");
        // get check_status from server
//        Log.d("ola", cur_status);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            textView.setText("Good morning!");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            textView.setText("Good afternoon!");
        } else {
            textView.setText("Good evening!");
        }
//        show_message("debugging purpose: cur_status = "+cur_status);
//        if (cur_status.equals("checkin")) {
//            show_message("You will be checking out now!");
//        }
//        else {
//            show_message("You will be checking in now!");
//        }
//        show_message("debug:  " + cur_status);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_status_func();
            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Add) {
                    check_status_func();
                }
                if (item.getItemId() == R.id.Alerts) {
                    finish();
                    Intent i = new Intent(check_status.this, Alerts.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.Profile) {
                    finish();
                    Intent i = new Intent(check_status.this, employee_dashboard.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.Logs) {
                    finish();
                    Intent i = new Intent(check_status.this, logs.class);
                    startActivity(i);
                }

                return true;
            }
        });
//        exit_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                show_message("Logged out successfully!");
//                Intent i = new Intent(check_status.this, LoginActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
//        dashboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                show_message("Dashboard opened");
//                Intent i = new Intent(check_status.this, employee_dashboard.class);
//                startActivity(i);
//            }
//        });
    }

    public void show_message(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public String read_data(String filename) {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString(filename, "");
        return s1;
    }

    public void write_data(String filename, String data) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(filename, data);
        myEdit.commit();
    }

    public void check_status_func() {
        // do a post request
        String emp_no = read_data("emp_no");
        // get image of the user
        String url = "https://sih-smart-attendance.herokuapp.com/check_in_out_status";
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            json_rec.replaceAll("\\P{Print}", "");

                            String cur_status = json_rec;
                            Log.i("Response", "cur_status : " + cur_status);
                            write_data("check_status", cur_status);

                            if (cur_status.equals("") || cur_status == null) {
                                Log.i("inside", "inside");
                                write_data("check_status", "checkout");
                                Log.i("After change", read_data("check_status"));
                                cur_status = read_data("check_status");
                            }
                            if (cur_status.equals("\"CHECKED OUT\"")) {

                                // proceed ...
                                show_message("Recording your entry attendance now!");
                                Intent i = new Intent(check_status.this, geoActivity.class);
                                startActivity(i);

                            } else {
                                // proceed ...
                                show_message("Recording your exit attendance now!");
                                Intent i = new Intent(check_status.this, geoActivity.class);
                                startActivity(i);
                            }
                        } catch (Exception e) {

                            Log.i("RESPONSE", "ERROR");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RESPONSE", "error : " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emp_no", emp_no);
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
