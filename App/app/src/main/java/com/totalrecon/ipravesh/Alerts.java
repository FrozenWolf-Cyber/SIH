package com.totalrecon.ipravesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Alerts extends AppCompatActivity {
    BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit2);
        setContentView(R.layout.activity_alerts);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Add) {
                    Intent intent = new Intent(Alerts.this, cameraActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.Logs) {
                    Intent i = new Intent(Alerts.this, logs.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.Profile) {
                    Intent i = new Intent(Alerts.this, employee_dashboard.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.house) {
                    Intent i = new Intent(Alerts.this, check_status.class);
                    startActivity(i);
                }

                return true;
            }
        });
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
}