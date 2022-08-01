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

public class get_password extends AppCompatActivity{
    private Button button;
    private TextView textView;
    private EditText editText1;

    public class idWithEmbeds {
        public String user_id;
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
        setContentView(R.layout.existing_user);
        button=(Button)findViewById(R.id.login);
        editText1 = (EditText) findViewById(R.id.password);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("EXISTING USER");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = editText1.getText().toString();
                String username = read_data("user_name");
                String upload_URL = "https://sih-smart-attendance.herokuapp.com/login";
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            idWithEmbeds obj2 = new idWithEmbeds();
                            String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            json_rec.replaceAll("\\P{Print}","");
                            Log.i("RESPONSE ",json_rec);

                            String resp1 = "\"0\"";
                            if (json_rec.equals(resp1)) {
                                show_message("Your password is wrong! ");
                            }
                            else {
                                show_message("You have been logged in!");

                                // Get existing user embeds
                                SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
                                String s = sh.getString("json", "");
                                Log.i("json", s);

                                if (s.equals("")) {
                                    obj2.user_id = "";
                                }
                                else {
                                    Gson gson = new Gson();
                                    obj2 = gson.fromJson(s, idWithEmbeds.class);
                                }
//
//                                obj2.user_id = checkNull(obj2.user_id);

                                Log.i("USERID in obj2",obj2.user_id);
                                json_rec.replaceAll("\"","");

                                if (obj2.user_id.equals(json_rec)) {
                                    // Embeds already there
                                }
                                else {
                                    String embeds_URL = "https://sih-smart-attendance.herokuapp.com/get_embed";
                                    VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, embeds_URL, new Response.Listener<NetworkResponse>() {
                                        @Override
                                        public void onResponse(NetworkResponse response) {
                                            try {
                                                // Getting embeds from server
                                                String embeds_res = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                                Log.i("json",embeds_res);
                                                Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                                                gson2.toJson(embeds_res);
                                                Gson gson = new Gson();
                                                onlyEmbeds object_em = gson.fromJson(embeds_res, onlyEmbeds.class);
                                                // Clearing any data in EmbedsSharedPref
                                                SharedPreferences sharedPreferences = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
                                                sharedPreferences.edit().clear().commit();
                                                // Save new user's embeds in EmbedsSharedPref
                                                idWithEmbeds theembeds = new idWithEmbeds();
                                                theembeds.user_id = json_rec;
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
                                            params.put("user_id", json_rec);
                                            return params;
                                        }
                                    };
                                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
                                }
                                Intent i = new Intent(get_password.this, check_status.class);
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
                        params.put("user_name_or_mail_id", username);
                        params.put("type_of_login", "username");
                        params.put("password",pass);
                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
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