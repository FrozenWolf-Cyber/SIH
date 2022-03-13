package com.example.myapplication.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.geoActivity;
import com.example.myapplication.geoActivity1;
import com.example.myapplication.register;
import com.example.myapplication.ui.login.LoginViewModel;
import com.example.myapplication.ui.login.LoginViewModelFactory;
import com.example.myapplication.databinding.ActivityLoginBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //clear_data();
        show_message(show_data());
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                //finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                 */
                String user_name = usernameEditText.getText().toString();
                String pass_word = passwordEditText.getText().toString();
                // search in text file ...
                //show_message("username:"+user_name+"password : "+pass_word);

                String full_data = show_data();
                String search_str = user_name+" "+pass_word;
                if(search(search_str , full_data))
                {

                    show_message("You have logged in successfully!");

                    write_data_local("Cur_User" , user_name);
                    Intent i = new Intent(LoginActivity.this, geoActivity.class);
                    startActivity(i);
                    finish();

                }
                else if (search(user_name , full_data))
                {
                    show_message("Check your username or passsword !\nIf you are new user ,sorry this email-id is already registered");

                }
                else{
                    show_message("New User Welcome!");
                    Intent i = new Intent(LoginActivity.this, geoActivity1.class);
                    startActivity(i);
                    write_data_local("New_User" , user_name);
                }

            }
        });
    }

    public boolean search(String s1 , String s2){
        return s2.contains(s1);
    }

    public void write_data_local(String filename , String val){
        try {
            FileOutputStream fOut = openFileOutput(filename,MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(val);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            show_error("error1 "+e);
            e.printStackTrace();
        }
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
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    private void write_data(String s) {
        String y = show_data();
        try {
            String fileName = "user_pass.txt";
            File f = new File(getApplicationContext().getFilesDir().getPath()+ File.separator + fileName);

                FileWriter lfilewriter = new FileWriter(f);
                BufferedWriter lout = new BufferedWriter(lfilewriter);
                lout.write(y+s+"\n");
                lout.close();
        } catch (IOException e) {
            show_message("write to sd card : "+e);
        }
    }
    public String show_data(){
        // Accessing the saved data from the downloads folder
        try {


            String fileName = "user_pass.txt";
            BufferedReader in = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), fileName)));
            String line = "";

            String data = "";
            while ((line = in.readLine()) != null)
                data += line;

            if (data != null) {
                return data;
            } else {
                show_message("No Data Found");
            }
        }
        catch(Exception e){
            show_message("error "+e);

        }
        return "";
    }
    public void clear_data()
    {
        try {
            String fileName = "user_pass.txt";
            File f = new File(getApplicationContext().getFilesDir().getPath()+ File.separator + fileName);

            FileWriter lfilewriter = new FileWriter(f);
            BufferedWriter lout = new BufferedWriter(lfilewriter);
            lout.write("\n");
            lout.close();
        } catch (IOException e) {
            show_message("write to sd card : "+e);
        }

        try {
            String fileName = "log.txt";
            File f = new File(getApplicationContext().getFilesDir().getPath()+ File.separator + fileName);

            FileWriter lfilewriter = new FileWriter(f);
            BufferedWriter lout = new BufferedWriter(lfilewriter);
            lout.write("\n");
            lout.close();
        } catch (IOException e) {
            show_message("write to sd card : "+e);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}