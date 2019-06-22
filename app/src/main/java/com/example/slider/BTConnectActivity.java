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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.Set;

public class BTConnectActivity extends AppCompatActivity {

    ArrayAdapter itemsAdapter1;
    ArrayAdapter itemsAdapter2;
    BluetoothAdapter mBTAdapter;
    Set<BluetoothDevice> pairedDevices;
    ProgressBar progress;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnectactivity);

        toolbar = findViewById(R.id.BTtoolbar);
        setSupportActionBar(toolbar);

        progress = findViewById(R.id.BTProgress);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBTAdapter.getBondedDevices();
        /*Affichage des périfériques déjà enregistrés*/
        AffichagePeripheriqueAppaire();

        /*recherche de périphériques*/
        itemsAdapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        RecherchePeripherique();

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(ouverture_bluetooth, filter1);
        this.registerReceiver(ouverture_bluetooth, filter2);

        ListView viewBTRecherche = findViewById(R.id.listView_appareils_decouverts);
        viewBTRecherche.setAdapter(itemsAdapter2);
    }

    private final BroadcastReceiver ouverture_bluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice deviceBT;
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    deviceBT = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    progress.setVisibility(View.VISIBLE);
                    itemsAdapter2.add(deviceBT.getName() + "\n" + deviceBT.getAddress());
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    progress.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    private void AffichagePeripheriqueAppaire() {
        itemsAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        ListView viewBTAppaire = findViewById(R.id.listView_app_appaires);
        viewBTAppaire.setAdapter(itemsAdapter1);//liaison de l'adapter à la view

        //affichage des appareils appairés, s'il en existe
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                itemsAdapter1.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    private void RecherchePeripherique() {
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        mBTAdapter.startDiscovery();
    }
}
