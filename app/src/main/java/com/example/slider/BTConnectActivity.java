package com.example.slider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BTConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnectactivity);

       Toolbar toolbar = findViewById(R.id.BTtoolbar);
       setSupportActionBar(toolbar);

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(ouverture_bluetooth,filter1);
        registerReceiver(ouverture_bluetooth,filter2);
        ArrayAdapter itemsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        ListView view1 = findViewById(R.id.listView_app_appaires);
        itemsAdapter.add(view1);

    }

    final static BroadcastReceiver ouverture_bluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
