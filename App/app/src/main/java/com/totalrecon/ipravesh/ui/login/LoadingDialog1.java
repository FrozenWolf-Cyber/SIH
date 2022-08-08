package com.totalrecon.ipravesh.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.totalrecon.ipravesh.R;
import com.totalrecon.ipravesh.uploadsignup;

public class LoadingDialog1 {

        private Activity activity;
        private AlertDialog dialog;

        public LoadingDialog1(uploadsignup myActivity)
        {
            activity = myActivity;
        }

        public void startLoadingDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.customdialogbox, null));

            dialog= builder.create();
            dialog.show();
        }


        public void dismissDialog(){
            dialog.dismiss();

        }



}
