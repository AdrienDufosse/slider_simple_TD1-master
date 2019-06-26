package com.example.slider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BTManager extends Transceiver{

    //Classe interne : ConnectThread
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private BluetoothDevice mBluetoothDevice;
    private ConnectThread mThread_de_connexion;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    public void manageMyConnectedSocket(BluetoothSocket mmSocket){
        int a =1;
    }

    @Override
    public void attachFrameProcessor() {

    }

    @Override
    public void detachFrameProcessor() {

    }

    @Override
    public void connect(Object device_adress) {
        String device_adress_str = (String) device_adress;
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(device_adress_str);
        try{
            mThread_de_connexion = new ConnectThread(mBluetoothDevice);
            mThread_de_connexion.run();
        }
        catch (Exception e){
            Exception exception = e;
            System.out.println(e);
        }


    }

    @Override
    public void getState() {

    }

    @Override
    public void setState() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void send() {

    }
}