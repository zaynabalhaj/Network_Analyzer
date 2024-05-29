package com.example.testtest;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Iterator;import android.widget.EditText;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {
    EditText startHourEditText, endHourEditText;
    EditText startDateEditText, endDateEditText;
    TextView textViewStartTime, textViewEndTime, textViewStartDate, textViewEndDate;

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_AND_LOCATION = 1;
    private TextView response;
    private Handler handler;
    private Runnable runnable;
    private Button backButton;
    private Button btnback;
    private Button btnSendHttpRequest;

    private SharedPreferences sharedPreferences;
    private static final String PREF_DEVICE_NAME = "device_name";

    private static final String TAG = "MainActivity";
    private String url = "http://192.168.1.14:5000/get_statistics";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        response = findViewById(R.id.resultTextView);
        startHourEditText = findViewById(R.id.startHourEditText);
        btnSendHttpRequest = findViewById(R.id.btnSendRequest);
        endHourEditText = findViewById(R.id.endHourEditText);

        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);
        textViewStartDate = findViewById(R.id.textViewStartDate);
        textViewEndDate = findViewById(R.id.textViewEndDate);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Check if device name is already stored
        if (!sharedPreferences.contains(PREF_DEVICE_NAME)) {
            // If not stored, prompt the user to enter their device name
            promptUserForDeviceName();
        }




        // Set onClickListener for Start Time EditText
        startHourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startHourEditText);
            }
        });



        // Set onClickListener for End Time EditText
        endHourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(endHourEditText);
            }
        });



        // Set onClickListener for Start Date EditText
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateEditText);
            }
        });

        // Set onClickListener for End Date EditText
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateEditText);
            }
        });
        // Initialize response TextView
        btnSendHttpRequest = findViewById(R.id.btnSendRequest);
        if (btnSendHttpRequest != null) {
            // Set onClickListener for btnSendHttpRequest
            btnSendHttpRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String startDateStr = startDateEditText.getText().toString();
                    String endDateStr = endDateEditText.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date startDate = sdf.parse(startDateStr);
                        Date endDate = sdf.parse(endDateStr);

                        // Check if the end date is before the start date
                        if (endDate.before(startDate)) {

                            Toast.makeText(MainActivity.this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                        } else {
                            // Start ThirdActivity
                            setContentView(R.layout.design);

                            btnback = findViewById(R.id.returnButton);

                            btnback.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    setContentView(R.layout.activity_main);

                                    new HttpRequestStatTask().execute();
                                }
                            });

                            new HttpRequestStatTask().execute();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity when the button is clicked
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                startActivity(intent);
            }
        });



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_AND_LOCATION);
        } else {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    // Execute the AsyncTask to extract information
                    new NetworkTask().execute();
                }
            };
            handler.post(runnable); // Initial call
        }




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
            }
        });

        builder.setCancelable(false);
        builder.show();
    }
    private void showTimePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        editText.setText(timeFormat.format(calendar.getTime()));
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editText.setText(dateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);

        datePickerDialog.show();
    }


    public class HttpRequestStatTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {

                // Extract start date
                String startDate = startDateEditText.getText().toString();
                String startTimeHour = startHourEditText.getText().toString();

                String start = startDate + " " + startTimeHour + ":00";
                // Extract end date and time
                String endDate = endDateEditText.getText().toString();
                String endTimeHour = endHourEditText.getText().toString();

                String end = endDate + " " + endTimeHour + ":00";
                String device_name= sharedPreferences.getString(PREF_DEVICE_NAME, Build.MODEL);


                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("start_date", start);
                    jsonBody.put("end_date", end);
                    jsonBody.put("device_name",device_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }


                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();


                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return response.body().string(); // Return the response data
                } else {
                    return "Error";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: Response received: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                Log.d(TAG, "inside try: trying");

                // Retrieve values for G2_percentage, G3_percentage, G4_percentage, touch_percentage, and alfa_percentage
                double g2Percentage = jsonResponse.getDouble("G2_percentage");
                double g3Percentage = jsonResponse.getDouble("G3_percentage");
                double g4Percentage = jsonResponse.getDouble("G4_percentage");
                int touchPercentage = jsonResponse.getInt("touch_percentage");
                int alfaPercentage = jsonResponse.getInt("alfa_percentage");


                JSONObject avgSignalPowerPerType = jsonResponse.getJSONObject("avg_signal_power_per_type");
                double g2spValue = avgSignalPowerPerType.optDouble("2G", 0.0); // Use optDouble to handle the absence of "2G"
                double g3spValue = avgSignalPowerPerType.optDouble("3G", 0.0);
                double g4spValue = avgSignalPowerPerType.optDouble("4G", 0.0); // Use optDouble to handle the absence of "4G"

                JSONObject avgSNRPerNetworkType = jsonResponse.getJSONObject("avg_snr_per_network_type");
                double snr3GValue =  avgSNRPerNetworkType.optDouble("3G", 0.0); // SNR value for 3G network
                double snr2GValue = avgSNRPerNetworkType.optDouble("2G", 0.0); // SNR value for 2G network
                double snr4GValue = avgSNRPerNetworkType.optDouble("4G", 0.0); // SNR value for 4G network



                Log.d(TAG, "onPostExecute: " + snr3GValue);
                Log.d(TAG, "onPostExecute: " + snr2GValue);
                Log.d(TAG, "onPostExecute: " + snr4GValue);

                TextView snr2GTextView = findViewById(R.id.snr2g);
                TextView snr3GTextView = findViewById(R.id.snr3g);
                TextView snr4GTextView = findViewById(R.id.snr4g);

                snr2GTextView.setText(String.valueOf(snr2GValue));
                snr3GTextView.setText(String.valueOf(snr3GValue));
                snr4GTextView.setText(String.valueOf(snr4GValue));

                // Find the TextViews with corresponding ids
                TextView g2TextView = findViewById(R.id.g2conn);
                TextView g3TextView = findViewById(R.id.g3conn);
                TextView g4TextView = findViewById(R.id.g4conn);
                TextView touchTextView = findViewById(R.id.touchconn);
                TextView alfaTextView = findViewById(R.id.alfaconn);

                // Find the TextViews with corresponding ids
                TextView g2spTextView = findViewById(R.id.g2sp);
                TextView g3spTextView = findViewById(R.id.g3sp);
                TextView g4spTextView = findViewById(R.id.g4sp);

                // Set the values to the corresponding TextViews
                g2spTextView.setText(String.valueOf(g2spValue));
                g3spTextView.setText(String.valueOf(g3spValue));
                g4spTextView.setText(String.valueOf(g4spValue));


                // Set the values to the corresponding TextViews
                g2TextView.setText(String.valueOf(g2Percentage));
                g3TextView.setText(String.valueOf(g3Percentage));
                g4TextView.setText(String.valueOf(g4Percentage));
                touchTextView.setText(String.valueOf(touchPercentage));
                alfaTextView.setText(String.valueOf(alfaPercentage));

                // Retrieve average signal power per device
                JSONObject avgSignalPowerPerDevice = jsonResponse.getJSONObject("avg_signal_power_per_device");

                // Get the TableLayout for device signal power
                TableLayout deviceSignalPowerTableLayout = findViewById(R.id.deviceSignalPowerTableLayout);

                // Iterate over the keys (device names) in avgSignalPowerPerDevice
                // Iterate over the keys (device names) in avgSignalPowerPerDevice
                Iterator<String> keys = avgSignalPowerPerDevice.keys();
                while (keys.hasNext()) {
                    String deviceName = keys.next();
                    double avgSignalPower = avgSignalPowerPerDevice.getDouble(deviceName);

                    // Create a new TableRow
                    TableRow row = new TableRow(MainActivity.this);

                    // Create TextViews for device name and average signal power
                    TextView deviceNameTextView = new TextView(MainActivity.this);
                    deviceNameTextView.setText(deviceName);
                    deviceNameTextView.setGravity(Gravity.CENTER);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    deviceNameTextView.setLayoutParams(params);

                    TextView avgSignalPowerTextView = new TextView(MainActivity.this);
                    avgSignalPowerTextView.setText(String.valueOf(avgSignalPower));
                    avgSignalPowerTextView.setLayoutParams(params);
                    avgSignalPowerTextView.setGravity(Gravity.CENTER);

                    // Add TextViews to the TableRow
                    row.addView(deviceNameTextView);
                    row.addView(avgSignalPowerTextView);

                    // Add TableRow to the TableLayout
                    deviceSignalPowerTableLayout.addView(row);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "onPostExecute: hi");

        }
    }
    public class NetworkTask extends AsyncTask<Void, Void, Void> {
        private String operator;
        private String network;
        private int cellId;
        private int snr;
        private int signalPower;
        private int frequencyBand;
        private String timeStamp;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Call the method to extract cell information
                String cellInfo = extractCellInformation();
                if (cellInfo != null) {
                    String[] parts = cellInfo.split(",");
                    operator = parts[0];
                    cellId = Integer.parseInt(parts[1]);
                    snr = Integer.parseInt(parts[2]);
                    signalPower = Integer.parseInt(parts[3]);
                    frequencyBand = Integer.parseInt(parts[4]);
                    timeStamp = parts[5];
                    String imei = parts[6];
                    network= parts[7];


                    send_info(operator, network,cellId, snr, signalPower, frequencyBand, timeStamp, imei);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Schedule the task to be called again after 10 seconds
            handler.postDelayed(runnable, 10000);
        }
    }


    public String extractCellInformation() throws IOException {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            String operator = telephonyManager.getNetworkOperatorName();
            String cellId = "";
            int snr = 0;
            int signalPower = 0;
            int frequencyBand = 0;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            }
            int networkType = telephonyManager.getNetworkType(); // Get network type

            String networkTypeName = getNetworkTypeName(networkType); // Get network type name
            String imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.getDefault());
            String timeStamp = sdf.format(new Date());


            // Get cell ID
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

    private void send_info(String operator,String network_type, int cellId, int snr, int signalPower, int frequencyBand, String timeStamp, String imei) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Retrieve device name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String device_name = sharedPreferences.getString("device_name", "");
        //String deviceName = sharedPreferences.getString(PREF_DEVICE_NAME, Build.MODEL);
        String ip_address = getIpAddress();

        RequestBody requestBody = new FormBody.Builder()
                .add("id", imei)
                .add("network_type",network_type)
                .add("operator", operator)
                .add("cellId", String.valueOf(cellId))
                .add("snr", String.valueOf(snr))
                .add("signalPower", String.valueOf(signalPower))
                .add("frequencyBand", String.valueOf(frequencyBand))
                .add("timeStamp", timeStamp)
                .add("device_name",device_name)
                .add("ip_address",ip_address)
                .build();




        Request request = new Request.Builder()
                .url("http://192.168.1.14:5000/insert")
                .post(requestBody)
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }


            String responseBody = response.body().string();
            Log.d("Response", responseBody);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_AND_LOCATION) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Execute the AsyncTask to extract information
                        new NetworkTask().execute();
                    }
                };
                handler.post(runnable); // Initial call
            } else {

                Toast.makeText(this, "Permission denied. Cannot extract cell and location information.", Toast.LENGTH_SHORT).show();
            }
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


    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        System.out.println("IPv4 Address: " + address.getHostAddress());
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
