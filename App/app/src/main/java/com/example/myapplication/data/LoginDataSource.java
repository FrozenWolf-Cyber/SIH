package com.example.myapplication.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.example.myapplication.data.model.LoggedInUser;
import com.example.myapplication.ui.login.LoginActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication



            LoggedInUser fakeUser = new LoggedInUser(UUID.randomUUID().toString(), "Jane Doe");

            return new Result.Success<>(fakeUser);

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
    private void writeToFile(String message)
    {
        
    }
    public void logout() {
        // TODO: revoke authentication
    }
}