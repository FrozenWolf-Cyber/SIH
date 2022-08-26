package com.totalrecon.ipravesh.ui.login;
import static androidx.camera.core.CameraX.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.totalrecon.ipravesh.AlarmReceiver;
import com.totalrecon.ipravesh.Alerts;
import com.totalrecon.ipravesh.LoadingDialog;
import com.totalrecon.ipravesh.R;
import com.totalrecon.ipravesh.check_status;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.totalrecon.ipravesh.register_new_employee_cred;
import com.totalrecon.ipravesh.threeshot;
import android.provider.Settings.Secure;
import android.widget.ToggleButton;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    private Button login_button;
    private EditText password, username;
    private TextView signup_button;


    public class idWithEmbeds {
        public String emp_no;
        public float[] embed1;
        public float[] embed2;
        public float[] embed3;
    }

    public class onlyEmbeds {
        public float[] embed1;
        public float[] embed2;
        public float[] embed3;
    }

    public void saveEmbedsToSP(idWithEmbeds obj) {
        SharedPreferences sharedPreferences = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json_string = gson.toJson(obj);
        myEdit.putString("json", json_string);
        myEdit.commit();
    }

    public String checkNull(String s) {
        return s == null ? "" : s;
    }

    public static String getDeviceId(Context context) {

        String id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Message();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_user_login_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);
        login_button = (Button) findViewById(R.id.login);
        signup_button = findViewById(R.id.textView34);


        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, register_new_employee_cred.class);
                startActivity(i);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass = password.getText().toString();
                String user = username.getText().toString().trim();

                if (pass.equals("") || user.equals("")) {
                    show_message("Please enter a proper username and password !");
                } else {


                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("user_name", user);
                    myEdit.commit();

                /*

                    4 post requests here :

                   1. check_username
                   2. login
                   3. get_info
                   4. get_embed

                 */

                    // check_username


                    LoadingDialog loadingDialog = new LoadingDialog();
                    loadingDialog.activity = LoginActivity.this;
                    loadingDialog.startLoadingDialog();

                    // using handler class to set time delay methods
                    Handler handler = new Handler();


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_username";
                            VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    try {

                                        String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                        json_rec.replaceAll("\\P{Print}", "");
                                        Log.i("RESPONSE ", json_rec);
                                        String resp = "\"YES\"";
                                        if (json_rec.equals(resp)) {

                                            // username_exist , do login
                                            // login
                                            String upload_URL = "https://sih-smart-attendance.herokuapp.com/login";

                                            VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                                                @Override
                                                public void onResponse(NetworkResponse response) {
                                                    try {
                                                        idWithEmbeds obj2 = new idWithEmbeds();
                                                        String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                                        json_rec.replaceAll("\\P{Print}", "");
                                                        Log.i("RESPONSE ", json_rec);

                                                        String resp1 = "\"INCORRECT PASSWORD\"";
                                                        String reps2 = "\"ACCOUNT ALREADY SIGNED IN\"";
                                                        if (json_rec.equals(resp1)) {
                                                            loadingDialog.dismissDialog();
                                                            show_message("Your password is wrong! ");
                                                        } else if (json_rec.equals(reps2)) {
                                                            loadingDialog.dismissDialog();
                                                            show_message("You have already logged in through a device!");
                                                        } else {
                                                            loadingDialog.dismissDialog();
                                                            show_message("You have been logged in! ");

                                                            // Get existing user embeds
                                                            SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
                                                            String s = sh.getString("json", "");
                                                            Log.i("json", s);

                                                            if (s.equals("")) {
                                                                obj2.emp_no = "";
                                                            } else {
                                                                Gson gson = new Gson();
                                                                obj2 = gson.fromJson(s, idWithEmbeds.class);
                                                            }
                                                            json_rec.replaceAll("\"", "");
                                                            String emp_no = json_rec;

                                                            // store employee number for further use
                                                            write_data("emp_no", json_rec);
                                                            // get_info
                                                            // get all details from user with emp_no
                                                            String upload_URL = "https://sih-smart-attendance.herokuapp.com/get_info";
                                                            VolleyMultipartRequest multipartRequest3 = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                                                                @Override
                                                                public void onResponse(NetworkResponse response) {
                                                                    try {
                                                                        String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                                                        json_rec.replaceAll("\\P{Print}", "");
                                                                        Map jsonObject = new Gson().fromJson(json_rec, Map.class);

                                                                        // fetched all data of user , stored in shared pref.

                                                                        String name = (String) jsonObject.get("name");
                                                                        write_data("name", name);
                                                                        String designation = (String) jsonObject.get("designation");
                                                                        write_data("designation", designation);
                                                                        String branch_name = (String) jsonObject.get("branch_name");
                                                                        write_data("branch_name", branch_name);
                                                                        String employeenumber = (String) jsonObject.get("emp_no");
                                                                        write_data("employeenumber", employeenumber);
                                                                        String gender = (String) jsonObject.get("gender");
                                                                        write_data("gender", gender);
                                                                        String phonenumber = (String) jsonObject.get("contact_no");
                                                                        write_data("phonenumber", phonenumber);
                                                                        String emailid = (String) jsonObject.get("mail_id");
                                                                        write_data("mail_id", emailid);


                                                                        Log.i("RESPONSE", "" + json_rec);
                                                                        Log.i("RESPONSE", "" + branch_name);

                                                                    } catch (UnsupportedEncodingException e) {
                                                                        loadingDialog.dismissDialog();
                                                                        show_message("Server Error");
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                                    , new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    loadingDialog.dismissDialog();
                                                                    show_message("Server Error");
                                                                    error.printStackTrace();
                                                                }
                                                            }) {
                                                                @Override
                                                                protected Map<String, String> getParams() {
                                                                    Map<String, String> params = new HashMap<>();
                                                                    params.put("emp_no", emp_no);
                                                                    return params;
                                                                }
                                                            };
                                                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest3);

                                                            if (json_rec.equals(obj2.emp_no)) {
                                                                // Embeds already there
                                                            } else {
                                                                // get embeddings
                                                                // get_embed
                                                                String embeds_URL = "https://sih-smart-attendance.herokuapp.com/get_embed";
                                                                VolleyMultipartRequest multipartRequest4 = new VolleyMultipartRequest(Request.Method.POST, embeds_URL, new Response.Listener<NetworkResponse>() {
                                                                    @Override
                                                                    public void onResponse(NetworkResponse response) {
                                                                        try {
                                                                            // Getting embeds from server
                                                                            String embeds_res = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                                                            Log.i("json", embeds_res);

                                                                            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                                                                            gson2.toJson(embeds_res);

                                                                            Gson gson = new Gson();
                                                                            onlyEmbeds object_em = gson.fromJson(embeds_res, onlyEmbeds.class);
                                                                            // Clearing any data in EmbedsSharedPref
                                                                            SharedPreferences sharedPreferences = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
                                                                            sharedPreferences.edit().clear().commit();
                                                                            // Save new user's embeds in EmbedsSharedPref
                                                                            idWithEmbeds theembeds = new idWithEmbeds();
                                                                            theembeds.emp_no = json_rec;
                                                                            theembeds.embed1 = object_em.embed1;
                                                                            theembeds.embed2 = object_em.embed2;
                                                                            theembeds.embed3 = object_em.embed3;
                                                                            saveEmbedsToSP(theembeds);

                                                                        } catch (UnsupportedEncodingException e) {
                                                                            loadingDialog.dismissDialog();
                                                                            show_message("server error");
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                }, new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        loadingDialog.dismissDialog();
                                                                        show_message("server error");
                                                                        error.printStackTrace();
                                                                    }
                                                                }) {
                                                                    @Override
                                                                    protected Map<String, String> getParams() {
                                                                        Map<String, String> params = new HashMap<>();
                                                                        params.put("emp_no", emp_no);
                                                                        return params;
                                                                    }
                                                                };
                                                                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest4);
                                                            }

                                                            // login verification complete
                                                            // go to next page check_status
                                                            loadingDialog.dismissDialog();

                                                            Intent i = new Intent(LoginActivity.this, check_status.class);
                                                            startActivity(i);
                                                        }

                                                    } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    error.printStackTrace();
                                                    loadingDialog.dismissDialog();
                                                    show_message("Server Error");
                                                }
                                            }) {
                                                @Override
                                                protected Map<String, String> getParams() {
//                                                    create_uuid();
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("user_name_or_mail_id", user);
                                                    params.put("type_of_login", "username");
                                                    params.put("password", pass);
                                                    params.put("mobileid", getDeviceId(getApplicationContext()));
                                                    return params;

                                                }
                                            };
                                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);

                                        } else {
                                            loadingDialog.dismissDialog();
//                        Log.i("Hey","I'm inside here");
                                            Toast.makeText(getApplicationContext(), "Username does not exist!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        loadingDialog.dismissDialog();
                                        show_message("server error");
                                        e.printStackTrace();
                                    }
                                }

                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loadingDialog.dismissDialog();
                                    show_message("Server Error");
                                    error.printStackTrace();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("username", user);
                                    return params;
                                }
                            };
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);

                        }
                    }, 2000);


//                    String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_username";
//                    VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
//                        @Override
//                        public void onResponse(NetworkResponse response) {
//                            try {
//
//                                String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                json_rec.replaceAll("\\P{Print}", "");
//                                Log.i("RESPONSE ", json_rec);
//                                String resp = "\"YES\"";
//                                if (json_rec.equals(resp)) {
//
//                                    // username_exist , do login
//                                    // login
//                                    String upload_URL = "https://sih-smart-attendance.herokuapp.com/login";
//
//                                    VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
//                                        @Override
//                                        public void onResponse(NetworkResponse response) {
//                                            try {
//                                                idWithEmbeds obj2 = new idWithEmbeds();
//                                                String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                                json_rec.replaceAll("\\P{Print}", "");
//                                                Log.i("RESPONSE ", json_rec);
//
//                                                String resp1 = "\"INCORRECT PASSWORD\"";
//                                                if (json_rec.equals(resp1)) {
//                                                    show_message("Your password is wrong! ");
//                                                } else {
//                                                    show_message("You have been logged in! ");
//
//                                                    // Get existing user embeds
//                                                    SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
//                                                    String s = sh.getString("json", "");
//                                                    Log.i("json", s);
//
//                                                    if (s.equals("")) {
//                                                        obj2.emp_no = "";
//                                                    } else {
//                                                        Gson gson = new Gson();
//                                                        obj2 = gson.fromJson(s, idWithEmbeds.class);
//                                                    }
//                                                    json_rec.replaceAll("\"", "");
//                                                    String emp_no = json_rec;
//
//                                                    // store employee number for further use
//                                                    write_data("emp_no", json_rec);
//                                                    // get_info
//                                                    // get all details from user with emp_no
//                                                    String upload_URL = "https://sih-smart-attendance.herokuapp.com/get_info";
//                                                    VolleyMultipartRequest multipartRequest3 = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
//                                                        @Override
//                                                        public void onResponse(NetworkResponse response) {
//                                                            try {
//                                                                String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                                                json_rec.replaceAll("\\P{Print}", "");
//                                                                Map jsonObject = new Gson().fromJson(json_rec, Map.class);
//
//                                                                // fetched all data of user , stored in shared pref.
//
//                                                                String name = (String) jsonObject.get("name");
//                                                                write_data("name", name);
//                                                                String designation = (String) jsonObject.get("designation");
//                                                                write_data("designation", designation);
//                                                                String branch_name = (String) jsonObject.get("branch_name");
//                                                                write_data("branch_name", branch_name);
//                                                                String employeenumber = (String) jsonObject.get("emp_no");
//                                                                write_data("employeenumber", employeenumber);
//                                                                String gender = (String) jsonObject.get("gender");
//                                                                write_data("gender", gender);
//                                                                String phonenumber = (String) jsonObject.get("contact_no");
//                                                                write_data("phonenumber", phonenumber);
//                                                                String emailid = (String) jsonObject.get("mail_id");
//                                                                write_data("mail_id", emailid);
//
//
//                                                                Log.i("RESPONSE", "" + json_rec);
//                                                                Log.i("RESPONSE", "" + branch_name);
//
//                                                            } catch (UnsupportedEncodingException e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        }
//                                                    }
//                                                            , new Response.ErrorListener() {
//                                                        @Override
//                                                        public void onErrorResponse(VolleyError error) {
//                                                            error.printStackTrace();
//                                                        }
//                                                    }) {
//                                                        @Override
//                                                        protected Map<String, String> getParams() {
//                                                            Map<String, String> params = new HashMap<>();
//                                                            params.put("emp_no", emp_no);
//                                                            return params;
//                                                        }
//                                                    };
//                                                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest3);
//
//                                                    if (json_rec.equals(obj2.emp_no)) {
//                                                        // Embeds already there
//                                                    } else {
//                                                        // get embeddings
//                                                        // get_embed
//                                                        String embeds_URL = "https://sih-smart-attendance.herokuapp.com/get_embed";
//                                                        VolleyMultipartRequest multipartRequest4 = new VolleyMultipartRequest(Request.Method.POST, embeds_URL, new Response.Listener<NetworkResponse>() {
//                                                            @Override
//                                                            public void onResponse(NetworkResponse response) {
//                                                                try {
//                                                                    // Getting embeds from server
//                                                                    String embeds_res = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                                                    Log.i("json", embeds_res);
//
//                                                                    Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
//                                                                    gson2.toJson(embeds_res);
//
//                                                                    Gson gson = new Gson();
//                                                                    onlyEmbeds object_em = gson.fromJson(embeds_res, onlyEmbeds.class);
//                                                                    // Clearing any data in EmbedsSharedPref
//                                                                    SharedPreferences sharedPreferences = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
//                                                                    sharedPreferences.edit().clear().commit();
//                                                                    // Save new user's embeds in EmbedsSharedPref
//                                                                    idWithEmbeds theembeds = new idWithEmbeds();
//                                                                    theembeds.emp_no = json_rec;
//                                                                    theembeds.embed1 = object_em.embed1;
//                                                                    theembeds.embed2 = object_em.embed2;
//                                                                    theembeds.embed3 = object_em.embed3;
//                                                                    saveEmbedsToSP(theembeds);
//
//                                                                } catch (UnsupportedEncodingException e) {
//                                                                    e.printStackTrace();
//                                                                }
//
//                                                            }
//                                                        }, new Response.ErrorListener() {
//                                                            @Override
//                                                            public void onErrorResponse(VolleyError error) {
//                                                                error.printStackTrace();
//                                                            }
//                                                        }) {
//                                                            @Override
//                                                            protected Map<String, String> getParams() {
//                                                                Map<String, String> params = new HashMap<>();
//                                                                params.put("emp_no", emp_no);
//                                                                return params;
//                                                            }
//                                                        };
//                                                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest4);
//                                                    }
//
//                                                    // login verification complete
//                                                    // go to next page check_status
//
//                                                    Intent i = new Intent(LoginActivity.this, check_status.class);
//                                                    startActivity(i);
//                                                }
//
//                                            } catch (UnsupportedEncodingException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }
//                                    }, new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            error.printStackTrace();
//                                        }
//                                    }) {
//                                        @Override
//                                        protected Map<String, String> getParams() {
//                                            Map<String, String> params = new HashMap<>();
//                                            params.put("user_name_or_mail_id", user);
//                                            params.put("type_of_login", "username");
//                                            params.put("password", pass);
//                                            return params;
//                                        }
//                                    };
//                                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);
//
//                                } else {
////                        Log.i("Hey","I'm inside here");
//                                    Toast.makeText(getApplicationContext(), "Username does not exist!", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("username", user);
//                            return params;
//                        }
//                    };
//                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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

    public void Message() {
        long time;
        Log.d("switchingon","toggle");
        if (1==1) {
            Log.d("switchingon1","toggle");
            //Toast.makeText(LoginActivity.this, "Notification Time Set", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            // calendar is called to get current time in hour and minute
            calendar.set(Calendar.HOUR_OF_DAY, 16);
            calendar.set(Calendar.MINUTE,23);
            calendar.set(Calendar.SECOND, 20);

            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            Intent intent = new Intent(this, AlarmReceiver.class);

            // we call broadcast using pendingIntent
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                Log.d("switchingon2","toggle");
                // setting time as AM and PM
                if (calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 86400000, pendingIntent);
        }else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(LoginActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }

    }




}