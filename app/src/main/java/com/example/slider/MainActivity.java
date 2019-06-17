package com.example.slider;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity implements Slider.SliderChangeListener {

    private final static int PERMISSIONS_REQUEST_CODE = 0;
    private final static int NO_ADAPTER = 0;
    private final static int USER_REQUEST = 1;
    private final static int PERMISSION_GRANTED = 2;
    private final static String[] PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int BT_ACTIVATION_REQUEST_CODE = 0;
    private static final int BT_CONNECT_CODE = 1;
    Slider mSlider;
    TextView mTextView;

    private android.bluetooth.BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlider = findViewById(R.id.myslider);

        mSlider.setSliderChangeListener(this);//NEW
        mTextView = findViewById(R.id.myvalue);
        if (savedInstanceState != null) {
            mTextView.setText(String.valueOf(savedInstanceState.getInt("myslidervalue")));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("myslidervalue", (int) mSlider.getValue());
    }

    @Override
    public void onChange(float value) {
        mTextView.setText(String.valueOf(value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.ButtonBluetoothConnection:
                switch (BluetoothRights()) {
                    case 1 : onBluetoothConfigRequest(false);
                    break;
                }
        }
        return true;
    }

    private int BluetoothRights() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            return NO_ADAPTER;
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!CheckMultiplePermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            return USER_REQUEST;
        }
        return PERMISSION_GRANTED;
    }

    private boolean CheckMultiplePermissions(String[] permissions) {
        if (permissions != null && permissions.length != 0) {
            for (String permission : permissions) { //opérateur ":" prend tous les éléments du tableau "permissions" et les affectes un par un à "permission"
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Autorisations nécessaires", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "Merci pour ces informations", Toast.LENGTH_SHORT).show();
            // activation BT si besoin
            onBluetoothConfigRequest(false);
        }
    }

    private void onBluetoothConfigRequest(boolean postActivationCall) {
        Intent BTActivation, BTConnect;

        //activation BT
        if (!mBluetoothAdapter.isEnabled()) {
            if (!postActivationCall) {
                BTActivation = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(BTActivation, BT_ACTIVATION_REQUEST_CODE);
            }
        } else {
            BTConnect = new Intent(this, BTConnectActivity.class);
            startActivityForResult(BTConnect, BT_CONNECT_CODE);
        }
    }
}




