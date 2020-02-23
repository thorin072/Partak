package com.delaquess.doodlz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeIntent = new Intent(this,MainActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
