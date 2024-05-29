package com.example.testtest;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplayActivity extends AppCompatActivity {
    TextView time_stamp, snr_, operator_, network_,frquency_band,signal_power,cell_id;
    private SharedPreferences sharedPreferences;
    private static final String PREF_DEVICE_NAME = "device_name";
    private NetworkChangeReceiver networkChangeReceiver;
    private String operator;
    private String network;
    private String cellId;
    private int snr;
    private int signalPower;
    private int frequencyBand;
    private String timeStamp;
    private String device_name;

    private static final int REFRESH_INTERVAL_MS = 10000; // 10 seconds

    private Handler mHandler;
    private Runnable mRefreshTask;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_AND_LOCATION = 1;
    private static final String TAG = "DISPLAY";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        time_stamp = findViewById(R.id.timeStampTextView);
        operator_ = findViewById(R.id.operatorTextView);
        snr_ = findViewById(R.id.snrTextView);
        network_ = findViewById(R.id.networkTypeTextView);
        signal_power = findViewById(R.id.signalPowerTextView);
        frquency_band = findViewById(R.id.frequencyTextView);
        cell_id = findViewById(R.id.idTextView);
        // Check if device name is stored in SharedPreferences, if not, prompt user
        if (!sharedPreferences.contains(PREF_DEVICE_NAME)) {
            promptUserForDeviceName();
        }

        // Initialize and register NetworkChangeReceiver
        networkChangeReceiver = new NetworkChangeReceiver(sharedPreferences);
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Check if permissions are granted
        if (!arePermissionsGranted()) {
            // Request permissions
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

        mHandler = new Handler(Looper.getMainLooper());
        mRefreshTask = new Runnable() {
            @Override
            public void run() {
                refreshData(); // Call method to refresh data
                mHandler.postDelayed(this, REFRESH_INTERVAL_MS); // refresh each interval
            }
        };

        // Start refreshing data
        mHandler.post(mRefreshTask);




        // Button to switch to MainActivity
        Button getStatisticsButton = findViewById(R.id.switchButton);
        getStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity when the button is clicked
                Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }





    private void promptUserForDeviceName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Device Name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String deviceName = input.getText().toString().trim();

                // Store the entered device name
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_DEVICE_NAME, deviceName);
                editor.apply();
                Log.d(TAG, "Device name stored in SharedPreferences: " + deviceName);

                // Extract cell information after device name is entered
                String cellInfo = null;
                try {
                    cellInfo = extractCellInformation();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Update UI if cell information is available
                if (cellInfo != null) {
                    String[] parts = cellInfo.split(",");
                    operator = parts[0];
                    cellId = parts[1];
                    snr = Integer.parseInt(parts[2]);
                    signalPower = Integer.parseInt(parts[3]);
                    frequencyBand = Integer.parseInt(parts[4]);
                    timeStamp = parts[5];
                    network = parts[7];

                    String frequencyBandString = String.valueOf(frequencyBand) + " MHz";

                    time_stamp.setText(timeStamp);
                    cell_id.setText(deviceName); // Update with entered device name
                    snr_.setText(String.valueOf(snr));
                    network_.setText(network);
                    frquency_band.setText(frequencyBandString);
                    signal_power.setText(String.valueOf(signalPower));
                    operator_.setText(operator);
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }




    private boolean arePermissionsGranted() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public String extractCellInformation() throws IOException {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            String operator = telephonyManager.getNetworkOperatorName(); // Get operator name
            String cellId = "";
            int snr = 0;
            int signalPower = 0;
            int frequencyBand = 0;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            int networkType = telephonyManager.getNetworkType(); // Get network type

            String networkTypeName = getNetworkTypeName(networkType); // Get network type name
            String imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
            String timeStamp = sdf.format(new Date());



            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
                if (cellInfoList != null) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                            cellId = String.valueOf(cellInfoLte.getCellIdentity().getCi());
                            snr = cellInfoLte.getCellSignalStrength().getRssnr();
                            signalPower = cellInfoLte.getCellSignalStrength().getDbm();
                            frequencyBand = cellInfoLte.getCellIdentity().getEarfcn();

                            return operator + "," + cellId + "," + snr + "," + signalPower + "," + frequencyBand + ","  + timeStamp + "," + imei + "," + networkTypeName;
                        } else if (cellInfo instanceof CellInfoGsm) {
                            CellIdentityGsm cellIdentityGsm = ((CellInfoGsm) cellInfo).getCellIdentity();
                            // Get the GSM frequency band
                            frequencyBand = cellIdentityGsm.getArfcn();

                            SignalStrength signalStrength = telephonyManager.getSignalStrength();
                            if (signalStrength != null) {
                                // Get the GSM signal strength in dBm
                                signalPower = signalStrength.getGsmSignalStrength();
                            }
                            return operator + "," + cellId + "," + snr + "," + signalPower + "," + frequencyBand + ","  + timeStamp + "," + imei + "," + networkTypeName;
                        } else if (cellInfo instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                            cellId = String.valueOf(cellInfoWcdma.getCellIdentity().getCid());
                            signalPower = cellInfoWcdma.getCellSignalStrength().getDbm();
                            frequencyBand = getWcdmaFrequencyBand(cellInfoWcdma);
                            return operator + "," + cellId + ",N/A," + signalPower + "," + frequencyBand + "," + timeStamp + "," + imei + ",3G";
                        }
                    }
                }
            } else {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_AND_LOCATION);
                return null;
            }
        }

        return null;
    }


    private int getWcdmaFrequencyBand(CellInfoWcdma cellInfoWcdma) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return -1;
        }

        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
        int uarfcn = cellIdentityWcdma.getUarfcn();

        if (uarfcn >= 412 && uarfcn <= 512) {
            return 2100; // UMTS Band I
        } else if (uarfcn >= 10562 && uarfcn <= 10838) {
            return 1900; // UMTS Band II
        } else if (uarfcn >= 9262 && uarfcn <= 9538) {
            return 850; // UMTS Band V
        } else if (uarfcn >= 9612 && uarfcn <= 9888) {
            return 1700; // UMTS Band IV
        } else {
            return -1;
        }
    }
    private void refreshData() {
        String cellInfo = null;
        try {
            cellInfo = extractCellInformation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        device_name= sharedPreferences.getString(PREF_DEVICE_NAME, Build.MODEL);
        // If cell information is available, update UI
        if (cellInfo != null) {
            String[] parts = cellInfo.split(",");
            operator = parts[0];
            cellId = parts[1];
            snr = Integer.parseInt(parts[2]);
            signalPower = Integer.parseInt(parts[3]);
            frequencyBand = Integer.parseInt(parts[4]);
            timeStamp = parts[5];
            network = parts[7];

            String frequencyBandString = String.valueOf(frequencyBand) + " MHz";

            time_stamp.setText(timeStamp);
            cell_id.setText(device_name);
            snr_.setText(String.valueOf(snr));
            network_.setText(network);
            frquency_band.setText(frequencyBandString);
            signal_power.setText(String.valueOf(signalPower));
            operator_.setText(operator);
            Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
        }
    }


    private String getNetworkTypeName(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // Inform the server to remove the device
        Log.d(TAG, "onReceive: will remove main");
        informServerToRemoveDevice();
    }


    public void informServerToRemoveDevice() {
        OkHttpClient client = new OkHttpClient();



        String imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);



        RequestBody requestBody = new FormBody.Builder()
                .add("device_id", imei)
                .build();


        Request request = new Request.Builder()
                .url("http://192.168.1.14:5000/disconnect")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to remove device from server: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Device removed from server successfully");
                } else {
                    Log.e(TAG, "Failed to remove device from server");
                }
            }
        });
    }
}
