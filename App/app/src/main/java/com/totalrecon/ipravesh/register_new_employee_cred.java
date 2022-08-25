package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class register_new_employee_cred extends AppCompatActivity {

    private Button button;
    private TextView button2;
    private EditText username , password , confirmpassword , empl_no;
    String user_name, pass_word, confirm_password, emplno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_newemployee_cred_new);

        button=findViewById(R.id.next);
        button2=findViewById(R.id.textView33);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        empl_no=findViewById(R.id.employee_number);
        confirmpassword=findViewById(R.id.confirmpassword);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // operation when the button is clicked...

                user_name = username.getText().toString();
                pass_word = password.getText().toString();
                confirm_password = confirmpassword.getText().toString();
                emplno = empl_no.getText().toString();

                /*
                    post requests
                    1. check_emp_no
                    2. check_username
                    all datas : emp_no , username ,password stored in SP for later use.
                    all datas verified : goto threeshot.java
                */


                int flag = 0;

                // some validation , input should never be empty
                if (user_name.replace(" ", "").equals("") ||
                        pass_word.replace(" ", "").equals("")) {
                    show_error("Please enter a non-empty username and password.");
                    flag = 1;
                }
                else if (emplno.replace(" ", "").equals("")) {
                    show_error("Please enter your employee number.");
                    flag = 1;
                }
                else if (!(pass_word.equals(confirm_password))) {
                    show_error("Confirm password does not match with new password!");
                    flag = 1;
                }
                // non-empty inputs given for emp_no , username , password
                // proceed for post request
                if (flag == 0) {


                    LoadingDialog loadingDialog = new LoadingDialog();
                    loadingDialog.activity = register_new_employee_cred.this;
                    loadingDialog.startLoadingDialog();

                    // using handler class to set time delay methods
                    Handler handler = new Handler();


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            // check_emp_no
                            // Post request for verification
                            String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_emp_no";

                            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    try {
                                        String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                        json_rec.replaceAll("\\P{Print}", "");
                                        JSONArray jArray = new JSONArray(json_rec);
                                        String existInMaster = jArray.getString(0);
                                        String existInLogin = jArray.getString(1);
                                        String resp1 = "YES";
                                        String resp2 = "NO";
                                        Log.i("RESPONSE", json_rec);
                                        if (resp1.equals(existInMaster)) {
                                            if (resp2.equals(existInLogin)) {
                                                write_data("emplno", "\"" + emplno + "\"");
                                                // emp_no is valid
                                                // check username , password

                                                String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_username";
                                                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                                                    @Override
                                                    public void onResponse(NetworkResponse response) {
                                                        try {
                                                            String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                                            json_rec.replaceAll("\\P{Print}", "");
                                                            String resp = "\"NO\"";
                                                            Log.i("RESPONSE", json_rec);
                                                            Log.i("resp", resp);


                                                            if (resp.equals(json_rec)) {
                                                                write_data("username", user_name);
                                                                write_data("password", pass_word);


                                                                // all details verified successfully
                                                                Intent i = new Intent(register_new_employee_cred.this, threeshot.class);
                                                                startActivity(i);
                                                            } else {
                                                                loadingDialog.dismissDialog();
                                                                show_error("This username is already taken! Please enter another username.");
                                                            }

                                                        } catch (UnsupportedEncodingException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        loadingDialog.dismissDialog();
                                                        show_error("Server Error");
                                                        error.printStackTrace();
                                                    }
                                                }) {
                                                    @Override
                                                    protected Map<String, String> getParams() {
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("username", user_name);
                                                        return params;
                                                    }
                                                };
                                                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
                                            } else {
                                                loadingDialog.dismissDialog();
                                                show_error("You have already been signed up! Please proceed to login.");
                                            }
                                        } else {
                                            loadingDialog.dismissDialog();
                                            show_error("Sorry, the given employee number is invalid!");
                                        }
                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loadingDialog.dismissDialog();
                                    show_error("Server Error");
                                    error.printStackTrace();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("emp_no", "\"" + emplno + "\"");
                                    return params;
                                }
                            };
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

                            //loadingDialog.dismissDialog();
                            //Intent i = new Intent(register_new_employee_cred.this, threeshot.class);
                            // starting finished activity
                            //startActivity(i);
                        }
                    }, 2000);


                    // check_emp_no
                    // Post request for verification
//                    String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_emp_no";
//
//                    VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
//                        @Override
//                        public void onResponse(NetworkResponse response) {
//                            try {
//                                String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                json_rec.replaceAll("\\P{Print}", "");
//                                JSONArray jArray = new JSONArray(json_rec);
//                                String existInMaster = jArray.getString(0);
//                                String existInLogin = jArray.getString(1);
//                                String resp1 = "YES";
//                                String resp2 = "NO";
//                                Log.i("RESPONSE", json_rec);
//                                if (resp1.equals(existInMaster)) {
//                                    if (resp2.equals(existInLogin)) {
//                                        write_data("emplno", "\"" + emplno + "\"");
//                                        // emp_no is valid
//                                        // check username , password
//
//                                        String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_username";
//                                        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
//                                            @Override
//                                            public void onResponse(NetworkResponse response) {
//                                                try {
//                                                    String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                                    json_rec.replaceAll("\\P{Print}", "");
//                                                    String resp = "\"NO\"";
//                                                    Log.i("RESPONSE", json_rec);
//                                                    Log.i("resp", resp);
//                                                    if (resp.equals(json_rec)) {
//                                                        write_data("username", user_name);
//                                                        write_data("password", pass_word);
//                                                        // all details verified successfully
//                                                        Intent i = new Intent(register_new_employee_cred.this, threeshot.class);
//                                                        startActivity(i);
//                                                    } else {
//                                                        show_error("This username is already taken! Please enter another username.");
//                                                    }
//
//                                                } catch (UnsupportedEncodingException e) {
//                                                    e.printStackTrace();
//                                                }
//
//                                            }
//                                        }, new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//                                                error.printStackTrace();
//                                            }
//                                        }) {
//                                            @Override
//                                            protected Map<String, String> getParams() {
//                                                Map<String, String> params = new HashMap<>();
//                                                params.put("username", user_name);
//                                                return params;
//                                            }
//                                        };
//                                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
//                                    } else {
//                                        show_error("You have already been signed up! Please proceed to login.");
//                                    }
//                                } else {
//                                    show_error("Sorry, the given employee number is invalid!");
//                                }
//                            } catch (UnsupportedEncodingException | JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("emp_no", "\"" + emplno + "\"");
//                            return params;
//                        }
//                    };
//                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
//
                }
            }
        });

    button2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(register_new_employee_cred.this, LoginActivity.class);
            startActivity(i);
        }
    });
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("Instance state","onsaveinstancestate");
        outState.putString("username",user_name);
        outState.putString("password",pass_word);
        outState.putString("confirm",confirm_password);
        outState.putString("empno",emplno);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("Instance state","restoresavedinstance");
        user_name = savedInstanceState.getString("username");
        pass_word = savedInstanceState.getString("password");
        confirm_password = savedInstanceState.getString("confirm");
        emplno = savedInstanceState.getString("empno");
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void show_error(String s) {

        // error due to file writing and other operations
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(register_new_employee_cred.this);
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



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        //super.onBackPressed();
        Intent a = new Intent(register_new_employee_cred.this, LoginActivity.class);
        startActivity(a);
    }
}