package com.totalrecon.ipravesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.totalrecon.ipravesh.R;
import com.totalrecon.ipravesh.data.model.VolleyMultipartRequest;
import com.totalrecon.ipravesh.data.model.VolleySingleton;
import com.totalrecon.ipravesh.ui.login.LoginActivity;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class uploadsignup extends AppCompatActivity {
    public class idWithArray {
        public String mail_id;
        public String user_name;
        public String password;
        public String name;
        public String designation;
        public String emp_no;
        public String gender;
        public String office_address;
        public String contact_no;
    }

    public class idWithEmbeds {
        public String user_id;
        public float[] embed1;
        public float[] embed2;
        public float[] embed3;
    }

    idWithArray obj = new idWithArray();
    idWithEmbeds user_embeds = new idWithEmbeds();

    public String read_data(String filename) {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString(filename, "");
        return s1;
    }

    public void readSP() {
        obj.mail_id = read_data("email");
        obj.user_name = read_data("user_name");
        obj.password = read_data("pass");
        obj.name = read_data("name");
        obj.designation = read_data("desig");
        obj.emp_no = read_data("emplno");
        obj.gender = read_data("gender");
        obj.office_address = read_data("office_add");
        obj.contact_no = read_data("phone");
    }

    public void saveEmbedsToSP(idWithEmbeds obj) {
        SharedPreferences sharedPreferences = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json_string = gson.toJson(obj);
        myEdit.putString("json", json_string);
        myEdit.commit();
    }

    public void clearSP() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    public boolean uploaded_state = true;
    /*
         By default: uploaded_state = true
         When "ALREADY IN USE" response is received, then uploaded_state = false
     */

    private String upload_URL = "https://sih-smart-attendance.herokuapp.com/signup";
    Bitmap headshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_final_upload);
        Button button = (Button) findViewById(R.id.button3);
        Button button1 = findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSP();
                SharedPreferences sh = getSharedPreferences("EmbedsSharedPref", MODE_PRIVATE);
                String s = sh.getString("json", "");
                Gson gson = new Gson();
                idWithEmbeds obj2 = gson.fromJson(s, idWithEmbeds.class);
                user_embeds.embed1 = obj2.embed1;
                user_embeds.embed2 = obj2.embed2;
                user_embeds.embed3 = obj2.embed3;

                // Retrieve the image stored in the fileOutputStream
                try {
                    headshot = BitmapFactory.decodeStream(openFileInput("myImage_1"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Upload to server
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            user_embeds.user_id = json_rec;
                            Log.i("Return from server: ", json_rec);
                            saveEmbedsToSP(user_embeds);
                            clearSP();
                            Intent i = new Intent(uploadsignup.this, LoginActivity.class);
                            startActivity(i);
                            finish();

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
                        params.put("mail_id", obj.mail_id);
                        params.put("user_name", obj.user_name);
                        params.put("password", obj.password);
                        params.put("name", obj.name);
                        params.put("designation", obj.designation);
                        params.put("emp_no", obj.emp_no);
                        params.put("gender", obj.gender);
                        params.put("office_address", obj.office_address);
                        params.put("contact_no", obj.contact_no);
                        params.put("embed1", Arrays.toString(user_embeds.embed1));
                        params.put("embed2", Arrays.toString(user_embeds.embed2));
                        params.put("embed3", Arrays.toString(user_embeds.embed3));
                        return params;
                    }

                    // Comment below getByteData() function for no image transfer to server
                    @Override
                    protected Map<String, DataPart> getByteData() throws IOException {
                        Map<String, DataPart> params = new HashMap<>();
                        Bitmap bitmap = headshot;
                        //ivSource.setImageBitmap(bitmap);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        params.put("files", new DataPart("file.jpg", stream.toByteArray(), "image/jpg"));
                        return params;
                    }
                };
                multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

            }

        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(uploadsignup.this, threeshot.class);
                startActivity(i);
            }
        });
    }
}
