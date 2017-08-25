package dev.votu.rfatvapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class BluetoothConnection extends Thread {

    private final BluetoothSocket mmSocket;
    private final Semaphore RunningSemaphore = new Semaphore(0);
    private final SynchronousQueue<String> BRIMsgQueue = new SynchronousQueue<String>();
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    private BlockingQueue<String> mMsgQ;

    public BluetoothConnection(BluetoothDevice device,
                               BlockingQueue<String> MsgQ
    ) throws IOException {
        mMsgQ = MsgQ;

        // Cancel discovery because it will slow down the connection
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();

        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        // Use the "well-known" SPP UUID
        tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            tmp.connect();
            mInStream = tmp.getInputStream();
            mOutStream = tmp.getOutputStream();
        } catch (IOException e) {
            Log.e("BluetoothConnection", "IO exception connectException: " + e.getMessage());
            // Unable to connect; close the socket and get out
            tmp.close();
            throw e;
        }
        mmSocket = tmp;
    }

    public String SendBRICommand(String Command, int TimeoutMilliseconds) {
        RunningSemaphore.acquireUninterruptibly();
        if (mOutStream != null) {
            Command += "\n";
            try {
                mOutStream.write(Command.getBytes());
                // wait for an "OK>" response, accumulate all response
                // output and return it to the caller
                try {
                    String Result = "";
                    while (true) {
                        String Line;
                        if ((Line = BRIMsgQueue.poll(TimeoutMilliseconds, TimeUnit.MILLISECONDS)) == null)
                            break;

                        Result += Line + "\n";
                        if (Line.equals("OK>")) {
                            RunningSemaphore.release();
                            return Result;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d("BluetoothConnection", "BRIMsgQueue.take interrupted: " + e.getMessage());
                }
            } catch (IOException e) {
                Log.d("BluetoothConnection", "IO exception writing stream: " + e.getMessage());
            }
        }
        RunningSemaphore.release();
        return null;
    }

    public void run() {
        // Keep listening to the InputStream until an exception occurs
        RunningSemaphore.release();
        BufferedReader BR = new BufferedReader(new InputStreamReader(mInStream), 1024);
        while (true) {
            try {
                String Line;
                if ((Line = BR.readLine()) != null) {
                    if (Line.startsWith("EVT:")) {
                        // Event messages are passed back through the parent's Handler
                        if (!mMsgQ.offer(Line)) {
                            // message dropped
                            Log.d("BluetoothConnection", "Dropped" + Line);
                        }
                    } else {
                        // Non-event messages are the result of a BRI command and are
                        // posted to the BRIMsgQueue to be consumed by SendBRICommand
                        try {
                            BRIMsgQueue.put(Line);
                        } catch (InterruptedException e) {
                            Log.d("BluetoothConnection", "BRIMsgQueue.put interrupted: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                Log.d("BluetoothConnection", "IO exception reading stream: " + e.getMessage());
                break;
            }
        }
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.d("BluetoothConnection", "IO exception closing socket: " + e.getMessage());
        }
    }
}
