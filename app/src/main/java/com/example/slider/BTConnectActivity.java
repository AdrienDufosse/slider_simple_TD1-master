package com.example.slider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

public class BTConnectActivity extends AppCompatActivity {

    Toolbar toolbar_osci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnectactivity);
        toolbar_osci = findViewById(R.id.toolbar);
        setSupportActionBar(R.id.toolbar);
    }
}
