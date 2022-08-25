package com.totalrecon.ipravesh.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.totalrecon.ipravesh.R;

public class LoadingDialog1 {

    public Activity activity;
    private AlertDialog dialog;

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.customdialogbox, null));

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}