package com.example.pingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);                    //set content view to layout activity_splash.xml file


        Thread thread = new Thread() {
            @Override
            public void run() {                                                          //lightweight process for displaying the splash_activity
                try {
                    sleep(1000);                                                     //for 1 seconds
                } catch (Exception e) {
                    e.printStackTrace();                                                   //for any exception might caught
                } finally {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);  //after 1sec move to main_activity
                    startActivity(mainIntent);                                             //method will start the main_activity
                    finish();                                   //finish this activity
                }
            }
        };
        thread.start();         //starts thread
    }
}
