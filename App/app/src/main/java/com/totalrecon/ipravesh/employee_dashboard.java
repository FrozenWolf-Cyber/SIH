package com.totalrecon.ipravesh;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.totalrecon.ipravesh.data.model.VolleySingleton;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.ui.login.LoginActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class employee_dashboard extends AppCompatActivity {
    PieChart pieChart;
    ImageView image;
    TextView name, emplno, desig, gender, officeaddress, phonenumber, email, logdetails;
    Button back;
    BottomNavigationView navView;
    Button exit_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);

        //clear_data();
        //show_message(show_data());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_profile);
        // pie chart
//        pieChart = findViewById(R.id.piechart);
//        // back button
//        back = findViewById(R.id.back);
        // text views for other details
        exit_button=(Button)findViewById(R.id.button7);
        name = findViewById(R.id.name);
        emplno = findViewById(R.id.eno);
        desig = findViewById(R.id.designation);
        gender = findViewById(R.id.gender);
        officeaddress = findViewById(R.id.office);
        phonenumber = findViewById(R.id.phone);
        logdetails = findViewById(R.id.logdetails);
        email = findViewById(R.id.email);
        // image view
        image = findViewById(R.id.imageView13);
        navView = findViewById(R.id.nav_view);

        show_log();
        setimage();
        show_all_datas();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Add) {
                    check_status_func();
                    Intent intent = new Intent(employee_dashboard.this, geoActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.Logs) {
                    Intent i = new Intent(employee_dashboard.this, logs.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.Alerts) {
                    Intent i = new Intent(employee_dashboard.this, Alerts.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.house) {
                    Intent i = new Intent(employee_dashboard.this, check_status.class);
                    startActivity(i);
                }

                return true;
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_message(Constant.logout_successful_msg);
                Intent i = new Intent(employee_dashboard.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i = new Intent(employee_dashboard.this , check_status.class);
//                startActivity(i);
//            }
//        });
    }
    public void show_all_datas()
    {
        name.setText(""+read_data("name"));
        emplno.setText(""+read_data("employeenumber"));
        desig.setText(""+read_data("designation"));
        gender.setText(""+read_data("gender"));
        officeaddress.setText(""+read_data("branch_name"));
        phonenumber.setText(""+read_data("phonenumber"));
        email.setText(""+read_data("mail_id"));
    }
    public void show_log()
    {
        String upload_URL = Constant.get_log_data_url;
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
                        set_pie_chart(present, 100 - present);

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
    public void setimage()
    {
        String emp_no = read_data("emp_no");
        // get image of the user
        String url = Constant.get_img_url;
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST , url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            // set image as bitmap to dashboard ...
                            byte[] bitmapdata = response.data;
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                            //String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            Log.i("DASHBOARD" , "received");
                            image.setImageBitmap(bitmap);

                        } catch (Exception e) {
                            show_message(Constant.server_error_msg);
                            Log.i("DASHBOARD" , "ERROR");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        show_message(Constant.server_error_msg);
                        Log.i("DASHBOARD","error");
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emp_no", emp_no);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
    public void set_pie_chart(float percent1 , float percent2)
    {
        pieChart.addPieSlice(
                new PieModel(
                        Constant.present_text,
                        percent1,
                        Color.parseColor(Constant.present_color)));
        pieChart.addPieSlice(
                new PieModel(
                        Constant.absent_text,
                        percent2,
                        Color.parseColor(Constant.absent_color)));
        pieChart.startAnimation();

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
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);/* w  w  w.ja va 2 s  .  c om*/
        return bmp;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                            Log.i("Response" , "cur_status : "+cur_status);
                            write_data("check_status" , cur_status);

                            if (cur_status.equals("") || cur_status==null) {
                                Log.i("inside", "inside");
                                Log.i("After change", read_data("check_status"));
                                cur_status = read_data("check_status");
                            }
                            if (cur_status.equals("\"CHECKED OUT\"")) {

                                // proceed ...
                                show_message(Constant.entry_attendance_msg);
                                Intent i = new Intent(employee_dashboard.this, geoActivity.class);
                                startActivity(i);

                            } else {
                                // proceed ...
                                show_message(Constant.exit_attendance_msg);
                                Intent i = new Intent(employee_dashboard.this, geoActivity.class);
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
                        Log.i("RESPONSE", "error : "+error);
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