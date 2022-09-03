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
import com.totalrecon.ipravesh.ui.login.LoginActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class logs extends AppCompatActivity {
    //    PieChart pieChart;
    TextView logdetails;
    PieChart pieChart;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logd);
        // pie chart
        pieChart = findViewById(R.id.piechart);
        logdetails = findViewById(R.id.logdetails2);
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Add) {
                    check_status_func();
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

    public void show_log() {
        String upload_URL = Constant.get_log_data_url;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {

                    String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    json_rec.replaceAll("\\P{Print}", "");
                    String resp = "\"NO\"";
                    Log.i("DASHBOARD", json_rec);
                    Map jsonObject = new Gson().fromJson(json_rec, Map.class);

                    try {
                        // receive datas of all employee
                        ArrayList<String> users = (ArrayList<String>) jsonObject.get("emp_no");
                        ArrayList<String> checkin = (ArrayList<String>) jsonObject.get("check_in");
                        ArrayList<String> checkout = (ArrayList<String>) jsonObject.get("check_out");
                        String log_data_detail = "";
                        String emp_no = read_data("emp_no");

                        // calculate percentage
//                        float present = 0;
////                        String array[] = new String[0];
//                        List<String> array = new ArrayList<String>();
                        for (int i = 0; i < users.size(); i++) {

                            if (("\""+users.get(i)+"\"").equals(emp_no)) {
                                log_data_detail += "Entry: "+checkin.get(i)+"\nExit: "+checkout.get(i)+"\n\n";
                            }
                            Log.i("RESPONSE", users.get(i));
                        }

//                        Calendar cal = Calendar.getInstance();
//                        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//                        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
//                        float absentperc = ((days-present) / days) * 100;
//                        float presentperc = (present / days) * 100;
//                        Log.i("a",Float.toString(absentperc));
//
//                        Log.i("RESPONSE", "percentage : " + present + " %");
//
//                        // percentage present in total

                        logdetails.setText(log_data_detail + "");
                        // set pie chart after percentage calculated
                        if (log_data_detail.equals("")) {
                            pieChart.addPieSlice(
                                    new PieModel(
                                                Constant.present_text,
                                            100,
                                            Color.parseColor(Constant.present_color)));
                            pieChart.startAnimation();
                        }
                        else {
                            set_pie_chart(100);
                        }

                    } catch (Exception e) {
                        Log.i("RESPONSE", e + "");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                show_message(Constant.server_error_msg);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // calculate attendance perentage for last month (30 days)
                params.put("last_n_days", String.valueOf(Constant.last_n_days));
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }
    public void set_pie_chart(float percent1)
    {
        pieChart.addPieSlice(
                new PieModel(
                        Constant.present_text,
                        percent1,
                        Color.parseColor(Constant.present_color)));
        pieChart.startAnimation();
    }
    public void check_status_func()
    {
        // do a post request
        String emp_no = read_data("emp_no");
        // get image of the user
        String url = Constant.check_in_out_status_url;
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
                                show_message(Constant.entry_attendance_msg);
                                Intent i = new Intent(logs.this, geoActivity.class);
                                startActivity(i);

                            } else {
                                // proceed ...
                                show_message(Constant.exit_attendance_msg);
                                Intent i = new Intent(logs.this, geoActivity.class);
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
        // Error due to file writing and other operations
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Exit();
                finishAffinity();
//                show_message("Logged out successfully!");
//                Intent i = new Intent(logs.this, LoginActivity.class);
//                startActivity(i);
//                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void Exit() {
//
//        logs.this.finish();
//   Intent a = new Intent(Intent.ACTION_MAIN);
//   a.addCategory(Intent.CATEGORY_HOME);
//    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    startActivity(a);
//
//    }
//    public void LoggedIn()
//    {
//        Intent i = new Intent(logs.this, LoginActivity.class);
//        startActivity(i);
//        finish();
//        //Exit();
//
//    }
}
