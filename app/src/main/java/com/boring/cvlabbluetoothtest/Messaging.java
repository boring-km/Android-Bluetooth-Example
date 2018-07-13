package com.boring.cvlabbluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Messaging {
    private String address; // 쓸 분들은 쓰세요
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private InputStream InputStream;

    public Messaging(String address, BluetoothAdapter adapter) {
        this.address = address;
        this.adapter = adapter;

        final BluetoothDevice device = adapter.getRemoteDevice(address);
        // 연결 설정까지!
        new Connection(device).start();
    }

    public class Connection extends Thread {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        private Connection(BluetoothDevice device) {
            bluetoothDevice = device;
        }
        @Override
        public void run() {
            try {
                BluetoothSocket temp = null;
                try {
                    temp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = temp;
                adapter.cancelDiscovery();
                try {
                    socket.connect();
                } catch (IOException connectException) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                InputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 보내기!
    public void send(String message) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(message.getBytes());
        outputStream.flush();
    }
    // 받기!
    public String receive() {

        byte[] buffer = new byte[256];
        String readMessage;
        int bytes;
        try {
            bytes = InputStream.read(buffer);
            readMessage = new String(buffer, 0, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        return readMessage;
    }
}
