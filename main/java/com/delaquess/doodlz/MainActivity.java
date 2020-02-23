package com.delaquess.doodlz;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;


public class MainActivity extends AppCompatActivity {


    // configures the screen orientation for this app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // determine screen size
        int screenSize =
                getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK;

        // use landscape for extra large tablets; otherwise, use portrait
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



    }
}
