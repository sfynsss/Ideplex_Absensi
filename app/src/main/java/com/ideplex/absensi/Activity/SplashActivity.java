package com.ideplex.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

import com.ideplex.absensi.R;
import com.ideplex.absensi.Session.Session;

public class SplashActivity extends AppCompatActivity {

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new Session(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.getUserLoggedIn()) {
                    Intent home = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(home);
                    finish();
                } else {
                    Intent home = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(home);
                    finish();
                }
            }
        }, 2000);

    }
}