package com.dukhaan.techy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class InitialSplashScreenActivity extends SharedPref {
    private static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUser == null)
                    startActivity(new Intent(InitialSplashScreenActivity.this, GoogleSignInActivity.class));
                else
                    startActivity(new Intent(InitialSplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_TIME);
    }
}