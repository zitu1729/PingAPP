package com.example.pingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;                         //import all necessary files


public class MainActivity extends AppCompatActivity {
    Thread thread;                                           //object for running a ui thread
    TextView textView;                                        //Text view which updates mobile data on or off
    Boolean flag;                                             //flag for checking the interrupt value for thread

    ExecutorService pool = Executors.newCachedThreadPool();           //creating a pool which starts threads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                   // set content layout to activity_main.xml file
        textView = findViewById(R.id.tv);                         //finding the id of the text_view field in the xml file

        thread = new Thread() {
            @Override
            public void run() {                               //thread which calls method m() in every 5.5 sec....
                flag = thread.isInterrupted();                    //initially thread interrupt flag is set false
                while (!flag) {                                   //loop checks the interrupt flag value
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                m();                            //calling user defined method m().
                            }
                        });
                        Thread.sleep(5500);                 //sleep this thread for every 5.5 sec
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //submitting the thread in the pool...
        pool.submit(thread);

    }


    //this is the actual method which checks the internet connectivity .First add two permissions to manifest file for internet access
    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    //<uses-permission android:name="android.permission.INTERNET" />

    public void m() {
        Boolean isConnected = false, isMobile = false, isWifi = false;                       //boolean flags for checking connection mobile data and Wifi
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE); //checks internet connection
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();                      //gives you network information

        if (activeNetwork != null) {                               //if network is active then
            isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE; //if Mobile data is ON then set isMobile = True
            isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI; //if Mobile data is ON then set isWifi = True
            isConnected = activeNetwork.isConnectedOrConnecting();          //if Internet is ON then set isConnected to True
        }

        //FOR MOBILE DATA
        if (isConnected && isMobile) {                          //if Both returns true then ...
            textView.setText("DATA ON");                        //set the text view to DATA ON
            if (isConnectedToThisServer())                      //showing a toast message for 2sec when a user defined function isConnectedToThisServer() returns True
                Toast.makeText(this, "Yes Connected to Google", Toast.LENGTH_SHORT).show(); //yes if connected to google
            else
                Toast.makeText(this, "No Google Connection", Toast.LENGTH_SHORT).show();//no connection when a user defined function isConnectedToThisServer() returns False
        } else {
            textView.setText("DATA OFF");                   //set the text view to DATA OFF when internet mobile data is off
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();   //showing  a toast message for 2 sec...
        }

        //FOR WIFI
        if (isConnected && isWifi) {                          //if Both returns true then ...
            textView.setText("WIFI ON");                        //set the text view to Wifi ON
            if (isConnectedToThisServer())             //showing a toast message for 2sec when a user defined function isConnectedToThisServer() returns True
                Toast.makeText(this, "Yes Connected to Google", Toast.LENGTH_SHORT).show(); //yes if connected to google
            else
                Toast.makeText(this, "No Google Connection", Toast.LENGTH_SHORT).show();//no connection when a user defined function isConnectedToThisServer() returns False

        } else {
            textView.setText("WIFI OFF");                   //set the text view to WIFI OFF when internet mobile data is off
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();   //showing  a toast message for 2 sec...
        }
    }

    public Boolean isConnectedToThisServer() {                //actual ping command works here..
        Runtime r = Runtime.getRuntime();
        try {
            Process ipProcess = r.exec("/system/bin/ping -c 1 8.8.8.8");         //here 8.8.8.8 is the default ip address of google server
            System.out.println("isConnectedToThisServer.. XXXXXXXXXXXXX");                  //this is use for logcat during testing
            int exitValue = ipProcess.waitFor();                                           //set exitvalue =0 for successful ping otherwise 1
            if (exitValue == 0)
                return true;                                              //returns True when exitvalue =0
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;                                                                    //returns false otherwise or unsuccessful
    }

    @Override
    protected void onResume() {
        super.onResume();
        pool.submit(thread);      //starts the thread when activity resumes again by submitting it to pool...
    }

    //if you want that your app continuously run in the background then remove this onPause() method
    @Override
    protected void onPause() {
        super.onPause();
        flag = true; //Interrupting the thread when you pause the activity
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        flag = true; //Interrupting the thread when you Back_pressed from Navigation menu
    }
}

