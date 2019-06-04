package com.example.slider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Slider mSlider;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlider = findViewById(R.id.myslider);
        mTextView = findViewById(R.id.myvalue);
        if (savedInstanceState != null) {
            mTextView.setText(String.valueOf(savedInstanceState.getInt("myslidervalue")));
        }

    }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("myslidervalue",(int)mSlider.getValue());
        }
    }



