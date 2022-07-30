package com.totalrecon.ipravesh.data;

import com.totalrecon.ipravesh.data.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

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

    idWithArray obj = new idWithArray();
    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication


            LoggedInUser fakeUser = new LoggedInUser(UUID.randomUUID().toString(), "Jane Doe");

            return new Result.Success<>(fakeUser);

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    private void writeToFile(String message) {

    }

    public void logout() {
        // TODO: revoke authentication
    }

//    public void usernameExists(String username) {
//    // save user_name
//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
//        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//        myEdit.putString("user_name", username);
//        myEdit.commit();
//
//        String upload_URL = "https://sih-smart-attendance.herokuapp.com/check_username";
//        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                try {
//                    String json_rec = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                    if (json_rec == "YES") {
//
//                        // Go to login password page
//                    }
//
//                    else {
//
//                        // Go to signup page
//
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_name", obj.user_name);
//                return params;
//            }
//        };
//
//    }
}