package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class logs extends AppCompatActivity {
//    PieChart pieChart;
    TextView logdetails;
    BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logd);
        // pie chart
//        pieChart = findViewById(R.id.piechart);
        logdetails = findViewById(R.id.logdetails2);
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Add) {
                    Intent intent = new Intent(logs.this, geoActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.Alerts) {
                    Intent i = new Intent(logs.this, Alerts.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.Profile) {
                    Intent i = new Intent(logs.this, employee_dashboard.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.house) {
                    Intent i = new Intent(logs.this, check_status.class);
                    startActivity(i);
                }

                return true;
            }
        });

        show_log();


    }

    public void show_log()
    {
        String upload_URL = "https://sih-smart-attendance.herokuapp.com/get_log_data";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {

                    String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    json_rec.replaceAll("\\P{Print}","");
                    String resp = "\"NO\"";
                    Log.i("DASHBOARD" , json_rec);
                    Map jsonObject = new Gson().fromJson(json_rec, Map.class);

                    try {
                        // receive datas of all employee
                        ArrayList<String> users = (ArrayList<String>) jsonObject.get("emp_no");
                        ArrayList<String> checkin = (ArrayList<String>) jsonObject.get("check_in");
                        ArrayList<String> checkout = (ArrayList<String>) jsonObject.get("check_out");
                        String log_data_detail = "";
                        String emp_no = read_data("emp_no");

                        // calculate percentage
                        float present = 0;
                        for (int i = 0; i < users.size(); i++) {
                            if (("\""+users.get(i)+"\"").equals(emp_no)) {
                                present += 1;
                                log_data_detail += "Entry: "+checkin.get(i)+"\nExit: "+checkout.get(i)+"\n\n";
                            }
                            Log.i("RESPONSE", users.get(i));
                        }

                        present = (present / 30) * 100;

                        Log.i("RESPONSE", "percentage : " + present + " %");

                        // percentage present in total

                        logdetails.setText(log_data_detail+"");
                        // set pie chart after percentage calculated
//                        set_pie_chart(present, 100 - present);

                    }
                    catch(Exception e)
                    {
                        Log.i("RESPONSE" , e+"");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                show_message("Server Error");
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // calculate attendance perentage for last month (30 days)
                params.put("last_n_days", "30");
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }
//    public void set_pie_chart(float percent1 , float percent2)
//    {
//        pieChart.addPieSlice(
//                new PieModel(
//                        "present",
//                        percent1,
//                        Color.parseColor("#28fc03")));
//        pieChart.addPieSlice(
//                new PieModel(
//                        "absent",
//                        percent2,
//                        Color.parseColor("#fc2403")));
//        pieChart.startAnimation();
//
//    }
    public String read_data(String filename)
    {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString(filename, "");
        return s1;
    }
    public void show_message(String s) {
        // Error due to file writing and other operations
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
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