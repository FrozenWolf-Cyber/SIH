package com.totalrecon.ipravesh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;
import com.totalrecon.ipravesh.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class otp_verification extends AppCompatActivity {

    private Button send_button , next_button;
    private TextView resend_button;
    private EditText otp_text;
    String otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_signup);

        next_button=(Button)findViewById(R.id.next);
        resend_button =  findViewById(R.id.resend_otp);
        otp_text=(EditText) findViewById(R.id.otp);

        // by default send -> enable , validate -> disable , resend -> disable
        next_button.setEnabled(true);
        resend_button.setEnabled(true);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // operation when the button is clicked...

                // make send -> enable , validate -> disable , resend -> false
                next_button.setEnabled(true);
                resend_button.setEnabled(true);
                
                otp = otp_text.getText().toString();
                String emplno = read_data("emplno");
                Log.i("debug",emplno);
                String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_otp";
                //Log.i("Parameters : ",otp+" , "+emplno);
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            json_rec.replaceAll("\\P{Print}", "");
                            String resp1 = "\"VERIFIED\"";
                            String resp2 = "NO";
                            Log.i("RESPONSE", json_rec);
                            if (resp1.equals(json_rec)) {
                                // otp matching , user proceed to next page
                                Intent i = new Intent(otp_verification.this, threeshot.class);
                                startActivity(i);
                            } else {
                                // otp does not match
                                show_error("Sorry, wrong otp!");
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
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("emp_no",  emplno );
                        params.put("otp", "\"" + otp + "\"");
                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
            }
        });

        resend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // resend_button is clicked...
                // only post request to be done .
                Log.i("response" , "click");
                String emplno = read_data("emplno");
                String upload_URL = "https://sih-smart-attendance.herokuapp.com/send_otp";
                /*
                    send emp_no to server,
                    server generates otp and sends to the employee's email_id
                 */

                // Log.i("Parameters : ", emplno);
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            json_rec.replaceAll("\\P{Print}", "");
                            String resp1 = "YES";
                            String resp2 = "NO";
                            Log.i("RESPONSE", json_rec);
                            show_toast("OTP has been resend to your email!");

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
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("emp_no", ""+emplno+"" );
                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
            }
        });

    }

    public void show_error(String s) {

        // error due to file writing and other operations
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(otp_verification.this);
        alertDialogBuilder.setMessage(s)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
    public void show_toast(String s)
    {
        Toast.makeText(getApplicationContext(),""+s, Toast.LENGTH_LONG).show();
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