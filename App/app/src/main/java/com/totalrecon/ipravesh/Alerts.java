package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Alerts extends AppCompatActivity {
    BottomNavigationView navView;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);
        setContentView(R.layout.activity_alerts);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Add) {
                    check_status_func();
                }
                if (item.getItemId() == R.id.Logs) {
                    Intent i = new Intent(Alerts.this, logs.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.Profile) {
                    Intent i = new Intent(Alerts.this, employee_dashboard.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.house) {
                    Intent i = new Intent(Alerts.this, check_status.class);
                    startActivity(i);
                }

                return true;
            }
        });
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
                                Log.i("After change", read_data("check_status"));
                                cur_status = read_data("check_status");
                            }
                            if (cur_status.equals("\"CHECKED OUT\"")) {

                                // proceed ...
                                show_message("Recording your entry attendance now!");
                                Intent i = new Intent(Alerts.this, geoActivity.class);
                                startActivity(i);

                            } else {
                                // proceed ...
                                show_message("Recording your exit attendance now!");
                                Intent i = new Intent(Alerts.this, geoActivity.class);
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

    public void show_message(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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


    public void OnToggleClicked(View view) {
        long time;
        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(Alerts.this, "Notification Time Set", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            // calendar is called to get current time in hour and minute
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 12);
            calendar.set(Calendar.SECOND,20);

            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            Intent intent = new Intent(this, AlarmReceiver.class);

            // we call broadcast using pendingIntent
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                // setting time as AM and PM
                if (calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 100000, pendingIntent);
        }


    }
}