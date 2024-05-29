package com.example.testtest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    private static final String PREF_DEVICE_NAME = "device_name";
    private static final String PREF_IP_ADDRESS = "ip_address";
    private SharedPreferences sharedPreferences;

    public NetworkChangeReceiver(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public NetworkChangeReceiver() {
        // Default constructor
    }

    //whenever a new connection is established this will be called
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(TAG, "Device connected to a network");

            String ipAddress = MainActivity.getIpAddress();
            String deviceName = sharedPreferences.getString(PREF_DEVICE_NAME, Build.MODEL);
            String imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm", Locale.getDefault());
            String timeStamp = sdf.format(new Date());

            Log.d(TAG, "onReceive: " + ipAddress);
            sendDeviceInfoToServer(context, ipAddress, deviceName,imei,timeStamp);
        } else {
            Log.d(TAG, "Device disconnected from the network");
            //informServerToRemoveDevice();
            Log.d(TAG, "onReceive: will remove");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void sendDeviceInfoToServer(Context context, String ipAddress, String deviceName,String imei,String timestamp){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("ip_address", ipAddress);
                    jsonBody.put("device_name", deviceName);
                    jsonBody.put("device_id",imei);
                    jsonBody.put("time_stamp",timestamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
                Request request = new Request.Builder()
                        .url("http://192.168.1.14:5000/new_connection")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Device info sent to server successfully");
                    } else {
                        Log.e(TAG, "Failed to send device info to server");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error sending device info to server: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }
}