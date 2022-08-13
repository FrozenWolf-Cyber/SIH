package com.totalrecon.ipravesh.ui.login;
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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.totalrecon.ipravesh.R;
import com.totalrecon.ipravesh.check_status;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity{
    private Button login_button , back_button;
    private TextView textView;
    private EditText password , username;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_user_login);

        login_button = (Button) findViewById(R.id.login);
        back_button = (Button) findViewById(R.id.backbutton);

        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("EXISTING USER");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, StartPage.class);
                startActivity(i);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass = password.getText().toString();
                String user = username.getText().toString();

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
                                            if (json_rec.equals(resp1)) {
                                                show_message("Your password is wrong! ");
                                            } else {
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

                                                            Log.i("RESPONSE", "" + json_rec);
                                                            Log.i("RESPONSE", "" + branch_name);

                                                        } catch (UnsupportedEncodingException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                        , new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
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
                                                            params.put("emp_no", emp_no);
                                                            return params;
                                                        }
                                                    };
                                                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest4);
                                                }

                                                // login verification complete
                                                // go to next page check_status

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
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("user_name_or_mail_id", user);
                                        params.put("type_of_login", "username");
                                        params.put("password", pass);
                                        return params;
                                    }
                                };
                                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);

                            } else {
//                        Log.i("Hey","I'm inside here");
                                Toast.makeText(getApplicationContext(), "Username does not exist!", Toast.LENGTH_SHORT).show();
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
                        params.put("username", user);
                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
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