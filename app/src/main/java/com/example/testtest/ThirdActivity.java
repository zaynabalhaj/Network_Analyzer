//package com.example.testtest;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Gravity;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.Iterator;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class ThirdActivity extends AppCompatActivity {
//    private TableLayout tableLayout;
//    private TextView textView;
//
//    private static final String TAG = "Result activity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.design);
//
//
//
//
//
//
//
//        // Find the TableLayout in the layout
////        tableLayout = findViewById(R.id.thirdTableLayout);
////
////        // Parse the JSON response to extract avg_signal_power_per_device
////        try {
////            JSONObject jsonResponse = new JSONObject(serverData);
////            JSONObject avgSignalPowerPerDevice = jsonResponse.getJSONObject("avg_signal_power_per_device");
////
////            // Iterate through each device in avg_signal_power_per_device and add it to the table
////            Iterator<String> keys = avgSignalPowerPerDevice.keys();
////            while (keys.hasNext()) {
////                String deviceName = keys.next();
////                int avgSignalPower = avgSignalPowerPerDevice.getInt(deviceName);
////
////                // Create a new TableRow
////                TableRow newRow = new TableRow(this);
////
////                // Create TextViews for device name and average signal power
////                TextView deviceNameTextView = new TextView(this);
////                deviceNameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
////                deviceNameTextView.setText(deviceName);
////                deviceNameTextView.setTextColor(Color.BLACK); // Set text color
////                deviceNameTextView.setPadding(10, 10, 10, 10); // Set padding
////                deviceNameTextView.setTextSize(12); // Set text size
////                deviceNameTextView.setGravity(Gravity.CENTER_HORIZONTAL); // Set text gravity
////                newRow.addView(deviceNameTextView); // Add device name TextView to TableRow
////
////                TextView avgSignalPowerTextView = new TextView(this);
////                avgSignalPowerTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
////                avgSignalPowerTextView.setText(String.valueOf(avgSignalPower));
////                avgSignalPowerTextView.setTextColor(Color.BLACK); // Set text color
////                avgSignalPowerTextView.setPadding(10, 10, 10, 10); // Set padding
////                avgSignalPowerTextView.setTextSize(12); // Set text size
////                avgSignalPowerTextView.setGravity(Gravity.CENTER_HORIZONTAL); // Set text gravity
////                newRow.addView(avgSignalPowerTextView); // Add average signal power TextView to TableRow
////
////                // Add TableRow to TableLayout
////                tableLayout.addView(newRow);
////            }
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//    }
////    public static void informServerToRemoveDevice() {
////        OkHttpClient client = new OkHttpClient();
////
////        //String localIpAddress = getLocalAddress();
////        //if (localIpAddress == null) {
////        // Handle error or fallback to a default IP address
////        //  return;
////        //}
////        //String url = "http://" + localIpAddress + ":5000/disconnect";
////
////        String ip_address = MainActivity.getIpAddress();
////
////        // Construct JSON object with IP address
////
////        RequestBody requestBody = new FormBody.Builder()
////                .add("ip_address", ip_address)
////                .build();
////
////
////        Request request = new Request.Builder()
////                .url("http://10.0.2.2:5000/disconnect")
////                .post(requestBody)
////                .build();
////
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Log.e(TAG, "Failed to remove device from server: " + e.getMessage());
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) throws IOException {
////                if (response.isSuccessful()) {
////                    Log.d(TAG, "Device removed from server successfully");
////                } else {
////                    Log.e(TAG, "Failed to remove device from server");
////                }
////            }
////        });
//    }

