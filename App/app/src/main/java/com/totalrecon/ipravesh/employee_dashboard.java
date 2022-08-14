package com.totalrecon.ipravesh;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.totalrecon.ipravesh.data.model.VolleySingleton;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;

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
    TextView name , emplno , desig , gender , officeaddress , phonenumber , logdetails;
    Button back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //clear_data();
        //show_message(show_data());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        // pie chart
        pieChart = findViewById(R.id.piechart);
        // back button
        back = findViewById(R.id.back);
        // text views for other details
        name = findViewById(R.id.name);
        emplno = findViewById(R.id.employeenumber);
        desig = findViewById(R.id.designation);
        gender = findViewById(R.id.gender);
        officeaddress = findViewById(R.id.officeaddress);
        phonenumber = findViewById(R.id.phonenumber);
        logdetails = findViewById(R.id.logdetails);
        // image view
        image = findViewById(R.id.photo);

        show_log();
        setimage();
        show_all_datas();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(employee_dashboard.this , check_status.class);
                startActivity(i);
            }
        });
    }
    public void show_all_datas()
    {
        name.setText(""+read_data("name"));
        emplno.setText(""+read_data("employeenumber"));
        desig.setText(""+read_data("designation"));
        gender.setText(""+read_data("gender"));
        officeaddress.setText(""+read_data("branch_name"));
        phonenumber.setText(""+read_data("phonenumber"));
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
    public void setimage()
    {
        String emp_no = read_data("emp_no");
        // get image of the user
        String url = "https://sih-smart-attendance.herokuapp.com/get_img";
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

                            Log.i("DASHBOARD" , "ERROR");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
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
                        "present",
                        percent1,
                        Color.parseColor("#28fc03")));
        pieChart.addPieSlice(
                new PieModel(
                        "absent",
                        percent2,
                        Color.parseColor("#fc2403")));
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

    }

}