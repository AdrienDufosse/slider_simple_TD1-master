package com.example.slider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.widget.LinearLayout;

public class BTConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnectactivity);

        Toolbar toolbar = findViewById(R.id.BTtoolbar);
        setSupportActionBar(toolbar);
    }
}
