package com.slimiot.walk.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.utils.HexUtil;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.slimiot.walk.BleAction.BluetoothConnection;
import com.slimiot.walk.BleOperations.BleActivities;
import com.slimiot.walk.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;



public class MainActivity extends AppCompatActivity {

    private String TAG = "READ_Prakhar";
    Context mContext;
    private TextView bleStateTimer;
    private BleDevice bleDevice;
    public static final String KEY_DATA = "key_data";
    long mSessionStartTime;
    private Button startIMU,exitIMU;
    private BroadcastReceiver mDeviceConnectionReciever;
    private BroadcastReceiver mRecieverIMU;
    private String userId, gender, dob;
    private int age, dobM, dobY, currentYear, currentMonth;
    private SharedPreferences sharedPreferences;
    private String genderValue = null , dobValue=null ;
    private int steps, totalStepsTakenSoFar;
    private boolean stepsCountButton;
    private float distance2Sec, distance = 0, speedKmPh;
    private double heightM,massKg;
    private int steps2Sec = 0, prevSteps = 0;

    private int vv = 0;
    private float first6StepsRead = 0, next6StepsRead = 0, step2SecCount = 0, distanceNext6StepRead = 0, distanceFirst6, distanceNext6, distanceFirst12;
    private float prevDistance = 0, currDistance, totalCalorieBurn = 0;
    private MovingAverage ma,movingAverageHR;
    Handler calorieHandler;
    Runnable calorieRunnable;
    private long timeStampCalUpDisTake,timeStampCalUpd,timeStampDiffCal;
    private TextView txtDisplaySteps,txtDisplayDistance,txtDisplayCalorie;
    public static String PACKAGE_NAME;
    private String weightUnit,heightUnit;
    private int intAbsHeight,intAbsWaist=0;
    private double absHeight=0,absWeight=0,absWaist=0,weight2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ma = new MovingAverage(3);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        sharedPreferences=this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
//        bottomNavigationView = findViewById(R.id.bottomNavigation);
        txtDisplaySteps = findViewById(R.id.txtDisplaySteps);
        txtDisplayCalorie = findViewById(R.id.txtDisplayCalorie);
        txtDisplayDistance = findViewById(R.id.txtDisplayDistance);
        getSavedData();
        mContext = this;
        bleStateTimer = findViewById(R.id.connectBleStateIMU);
        startIMU = findViewById(R.id.startIMU);
        exitIMU = findViewById(R.id.exitIMU);
        initData();
        bleStateTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = bleStateTimer.getText().toString();
                if(val.equals("Connect Now")){
                    Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        startIMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bleDevice == null){
                    Toast.makeText(MainActivity.this, "Device not connected", Toast.LENGTH_SHORT).show();
                }else {
                    BleActivities.onIMU();
////                    BleActivities.onHeartRate();
//                    if(!BleActivities.measureHRV) {
//                        BleActivities.onHeartRate();
//                        BleActivities.measureHRV = true;
////                        if(BleActivities.hrPPGState){
////                            // hr is already on no need to start//
////                         //   Toast.makeText(mContext, "hr is already on no need to start", Toast.LENGTH_SHORT).show();
////                            BleActivities.measureHRV = true;
////                        }else{
////                            // we have to start the hr && turn off the ppg.....
////                            try {
////                            //    Toast.makeText(mContext, "ppg turned off hr on", Toast.LENGTH_SHORT).show();
////                                BleActivities.measureHRV = true;
//////                                BleActivities.offPPG();
//////                                Thread.sleep(1000);
////                                BleActivities.onHeartRate();
////                            }catch (Exception e){
////
////                            }
////                        }
//                        if (countDownTimer != null) {
//                            countDownTimer.cancel();
//                        }
//                        startIMU.setAlpha(0.7f);
//                        countDownTimer.start();
//                        exitIMU.setText("Reset");
//                    }

                }
            }
        });
        exitIMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exitIMU.getText().toString().equals("Reset")){
//                    resetData();
                }else {
//                    BleActivities.measureHRV = false;
                    finish();
                }
            }
        });
        // for calorie
        calorieHandler = new Handler();
        calorieRunnable = new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
//                        Toast.makeText(mContext, "Hello 1 sec", Toast.LENGTH_SHORT).show();
                        Log.i("READ_Prakhar", "I am working continuous");
//                        freeFall();  // free fall check done
                        updateCalorie();
                    }
                });
//                handler.postDelayed(this, 30000); // Run this code again after 30 seconds
                calorieHandler.postDelayed(this, 30000);
            }
        };
//        calorieHandler.postDelayed(calorieRunnable, 4000);
        calorieHandler.postDelayed(calorieRunnable, 30000);

    }

    private void getSavedData() {
//        Toast.makeText(getApplicationContext(), genderValue, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), dobValue, Toast.LENGTH_SHORT).show();
        genderValue = sharedPreferences.getString("gender",null);

        if(genderValue!=null){
//            gender.setText(genderValue);
        }
        weightUnit = sharedPreferences.getString("weightUnit",null);
        heightUnit = sharedPreferences.getString("heightUnit",null);
//        Log.i("screen_m",sharedPreferences.getString("weightUnit",null));
        absWeight =  Double.parseDouble(sharedPreferences.getString("weight",null));
        absHeight =  Double.parseDouble(sharedPreferences.getString("height",null));


        if(heightUnit!=null) {
            if (heightUnit.equals("Centimeters")) {
                intAbsHeight = (int) absHeight;
                heightM=absHeight/100;
//                if(intAbsHeight != 0) {
//                    height.setText(intAbsHeight + " cm");
//                }
                //  height.setText(heightValue+" cm");
            } else if (heightUnit.equals("Feet & Inches")){
                heightM=absHeight*0.0254;
//                int x = (int) absHeight / 12;
//                int y = (int) Math.round(absHeight % 12);
//                height.setText(x + "'" + y + "''");
//                            Toast.makeText(ProfileActivity.this, "Hello", Toast.LENGTH_SHORT).show();
//                            if(x!=0&&y!=0) {
//                                height.setText(x + "'" + y + "''");
//                            }
            }
        }
        if(weightUnit!=null) {
            if (weightUnit.equals("Kilograms")) {
                if(absWeight!=0.0) {
                    massKg = absWeight;
//                    String s=new DecimalFormat("#####.#").format(absWeight);
//                    weight.setText(s + " kg");
                }
            }else if(weightUnit.equals("Pounds")){
                if(absWeight!=0.0) {
                    massKg=absWeight/2.20462;
//                    String s = new DecimalFormat("#####.#").format(absWeight);
//                    weight.setText(s + " lb");
                }
            }
        }
//        Log.i("parameter",massKg + " "+ heightM+" ");

    }



    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);

        BleActivities.mBleDevice = bleDevice;

        if (bleDevice == null) {
//            connectBle.setText("Connect Now");
            Toast.makeText(MainActivity.this, "Device not connected", Toast.LENGTH_SHORT).show();
            bleStateTimer.setText("Connect Now");
            startIMU.setAlpha(0.7f);
        } else {
//            connectBle.setText("Connected");
//            bleDevice = getIntent().getParcelableExtra(KEY_DATA);
//            if (bleDevice == null) {
////            bleStateTimer.setText("Connect Now");
//                bleStateTimer.setText("0:00 / 3:00");
//                startHRV.setAlpha(0.7f);
//            }else{
                bleStateTimer.setText("Connected");
//            }
            mSessionStartTime = new Date().getTime();
            BleActivities.setBleDevice(bleDevice);
            BleActivities.setContext(mContext);

            if(mDeviceConnectionReciever==null) {
//             Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                mDeviceConnectionReciever = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Handle the sensor data here
                        String deviceName = intent.getStringExtra("deviceName");
                        String deviceAddress = intent.getStringExtra("deviceAddress");
//                        connectBle.setText("Connect Now");
                        bleStateTimer.setText("Connect Now");
                        bleDevice = null;
                        startIMU.setAlpha(0.7f);
//                        resetData();
                        Toast.makeText(context, deviceName + " is disconnected", Toast.LENGTH_SHORT).show();
                        Log.i("READ_UI_DATA_Dev", deviceName + " " + deviceAddress + " disconnected");
                    }
                };
                IntentFilter filterDeviceConnection = new IntentFilter("DEVICE_DISCONNECTED");
                LocalBroadcastManager.getInstance(this).registerReceiver(mDeviceConnectionReciever, filterDeviceConnection);
            }
            // IMU

            mRecieverIMU = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Handle the sensor data here
                    byte[] data = intent.getByteArrayExtra("data");
                    String content = HexUtil.formatHexString(data, true);
                    Log.i("READ_UI_DATA_IMU", content);
                updateUI_IMU(data);
                }
            };
            IntentFilter filterIMU = new IntentFilter("sensor-dataIMU");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRecieverIMU, filterIMU);



        }
    }
    public void updateUI_IMU(byte[] data) {
        try {
//            if(data.length!=4) {
                int kAdd = 0;
                float[] AcStepDis = new float[4];
                float[] rotation = new float[3];
                String content = HexUtil.formatHexString(data, true);
                String[] arr = content.split(" ");
                Log.d("READ_Prakhar_IMU", content);
                String valueIMU = "";
                String tiltIMU = "";
                String mlcIMU = "";
                String stepsIMU = "";
                String totalStepsIMU = "";
                int activityIMU = Integer.parseInt(String.valueOf(arr[0+kAdd]), 16);
                int activityiMU = Integer.parseInt(String.valueOf(data[0+kAdd]), 16);
                if (activityIMU == 2) {
                    Log.i("READ_PrakharCheck_3", String.valueOf(activityIMU));
                }
                Log.i("READ_PrakharCheck_1", String.valueOf(activityIMU));
                Log.i("READ_PrakharCheck_2", String.valueOf(activityiMU));

                AcStepDis[0] = activityIMU;
                Log.i("READ_Prakhar_IMU", String.valueOf(activityIMU));


                if (activityIMU == 2) {
//                  Toast.makeText(mContext, "Steps Detected", Toast.LENGTH_SHORT).show();
                    Log.i("READ_Prakhar_IMU_", "Step Detector");
                    steps = Integer.parseInt(String.valueOf(arr[1+kAdd]), 16);

                    AcStepDis[1] = steps;

                    totalStepsTakenSoFar = totalStepsTakenSoFar + steps;
                    stepsIMU = String.valueOf(steps);
                    totalStepsIMU = String.valueOf(totalStepsTakenSoFar);
                    Log.i("READ_Prakhar_IMUSteps", String.valueOf(steps));
//                    Toast.makeText(mContext, "Steps Detected " + stepsIMU, Toast.LENGTH_SHORT).show();
                    //txtDisplaySteps.setText(stepsIMU);

                    // Displaying total steps :
                    txtDisplaySteps.setText(totalStepsIMU);

                    // Todays data updating
//                totalTodaySteps = steps;
//                    updateTodayData();
                    // Todays data updating finished
                    Log.d("READ_Prakhar_ActivityES","Each steps :" + String.valueOf(steps));
                    Log.i("READ_Prakhar_ActivityS","Total Steps :"+String.valueOf(totalStepsIMU));

                    Handler handler = new Handler();

                    // if one time data comes --> use count down timer.....for 24 secconds.... then after our same approcah...
                    Runnable runn = new Runnable() {

                        @Override
                        public void run() {
//                            steps2Sec = steps - prevSteps;
                            steps2Sec = totalStepsTakenSoFar - prevSteps;
                            prevSteps = totalStepsTakenSoFar;
                            if (steps2Sec == 0 && step2SecCount <= 12) {
                                steps2Sec = 2;
                            }
                            Log.i("READ_Prakhar_ActivitySS","Steps observed 2 sec :" +String.valueOf(steps2Sec));
//                            Log.i("Steps_in_2sec", String.alueOf(steps2Sec));
                            step2SecCount++;
                            if (steps2Sec == 0) {
                                stepsCountButton = false; // stop//
                            } else {
                                stepsCountButton = true; // run automatic
                            }
                            if (step2SecCount < 7) {
                                first6StepsRead = first6StepsRead + steps2Sec;
                                Log.i("READ_Prakhar_Activity","First 6 steps: "+first6StepsRead);
                            } else if (step2SecCount >= 7 && step2SecCount <= 12) {
                                next6StepsRead = next6StepsRead + steps2Sec;
                                if (heightM != 0) {
                                    distance2Sec = (float) Math.min(heightM * 1.15, (heightM * (0.0355 * Math.pow(steps2Sec, 2) - 0.1164 * steps2Sec + 0.4298)));
                                    // distance2sec == step length;
                                    // if  distance2Sec < 1.5 then not count
                                    // distance2Sec == steps in 2 sec == steps2sec;
                                    // moving average on steps2sec  || window size = 3 || steps2sec = number of steps in 2 sec

                                    distance2Sec = distance2Sec * steps2Sec; // distance covered in 2 seconds..........
                                    Log.i("READ_Prakhar_Activity", "Next 6 steps: "+String.valueOf(steps2Sec) + " " + String.valueOf(distance2Sec));
                                    distanceNext6StepRead = (int) (distance2Sec + distanceNext6StepRead);
                                }
                            }
                            if (step2SecCount == 11 || step2SecCount == 12) {
                                double average = ma.next(steps2Sec);
                                Log.i("READ_Prakhar_Activity","11 12 -> "+average);
                            } else if (step2SecCount >= 13) {
                                double average = ma.next(steps2Sec);
                                if (average < 1.5) {

                                } else {
                                    // steps2sec replace with average....
                                    if (heightM != 0) {
                                        distance2Sec = (float) Math.min(heightM * 1.15, (heightM * (0.0355 * Math.pow(average, 2) - 0.1164 * average + 0.4298)));
                                        distance2Sec = (float) (distance2Sec * average);
                                        Log.i("READ_Prakhar_Activity","Steps 2sec & dist aft 13 : " +String.valueOf(steps2Sec) + " " + String.valueOf(distance2Sec));
                                        distance = distance + distance2Sec;
                                    }
                                }
                            }
                            steps2Sec = 0;
                            if (stepsCountButton) {
                                handler.postDelayed(this, 2000);
                            }
                        }
                    };
                    if (!stepsCountButton) { // run if stop .. if running then no change...
                        stepsCountButton = true;
                        // this time is taken to find the time difference between the distance....for calculating the calorie
                        timeStampCalUpd = System.currentTimeMillis();

                        handler.postDelayed(runn, 2000);
                    }
                    // *****************..............Updating the UI from below here............************************
                    // Display distance after 1-6 No, 7-12 calculating, at 12 show first 12 then normal;
                    if (step2SecCount == 6) {

                    } else if (step2SecCount == 12) {

                        distanceNext6 = distanceNext6StepRead;
                        distanceFirst6 = (distanceNext6StepRead / next6StepsRead) * first6StepsRead;
                        distanceFirst12 = distanceFirst6 + distanceNext6;
                        Log.i("Steps_in_2sec_first6", String.valueOf(first6StepsRead));
                        Log.i("Steps_in_2sec_distF6", String.valueOf(distanceFirst6));
                        Log.i("Steps_in_2sec_distN6", String.valueOf(distanceNext6StepRead));
                        Log.i("Steps_in_2sec_distF12", String.valueOf(distanceFirst12));
                        AcStepDis[2] = distanceFirst12;
                        txtDisplayDistance.setText(String.valueOf((int) distanceFirst12)+" m");
                        // Todays data updating finished
                        currDistance = distanceFirst12;
                        // we are taking this time for calorie update && find the difference of time stamp to calculate the speed...
                        timeStampCalUpDisTake = System.currentTimeMillis();
                        //
                        distance = distanceFirst12;
                        // Todays data updating
//                        updateTodayData();
                    } else if (step2SecCount >= 13) {
                        AcStepDis[2] = distance;
                        txtDisplayDistance.setText(String.valueOf((int) distance)+" m");
                        // Todays data updating
//                    totalTodayDistance = distance;
//                        updateTodayData();
                        // Todays data updating finished
                        currDistance = distance;
                        // we are taking this time for calorie update && find the difference of time stamp to calculate the speed...
                        timeStampCalUpDisTake = System.currentTimeMillis();
                        //
                    }
                    AcStepDis[3] = totalCalorieBurn;
                    Intent intent = new Intent("sensor-dataIMU_Steps");
                    intent.putExtra("data_Steps", AcStepDis);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                }
//            }
        } catch (Exception e) {
            Log.i("READ_Prakhar_Error", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();


        // showPopUp - Want to disconnect device.  if yes send back to starting screen

//        if (BleManager.getInstance().isConnected(bleDevice)&&BleActivities.mBleDevice != null) {
//            showPopUPbackPressed();
//        }else{
//            Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }


        if (BleManager.getInstance().isConnected(bleDevice)&&BleActivities.mBleDevice != null) {
            disconnectDevicePopUp();
        }else{
            Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }




//        Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);


//        Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

//        finish();
//        disconnectDevicePopUp("Do you want to close the application?");



//        finish();
    }
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(calorieRunnable!=null){
            calorieHandler.removeCallbacks(calorieRunnable);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeviceConnectionReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRecieverIMU);
        BleActivities.offIMU();
//        BleActivities.measureHRV = false;
    }
    public void disconnectDevicePopUp(){
        final AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_disconnect_ble_device, null);
        d.setView(dialogView);
        final TextView ok =dialogView.findViewById(R.id.okDisconnectDevice);
        final TextView cancel=dialogView.findViewById(R.id.cancelDisconnectDevice);
        AlertDialog alertDialog = d.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(600, 400);
        alertDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
//                disconnectFromDevice();
//                unregisterServices();

                Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                alertDialog.dismiss();

//                alertDialog.dismiss();
//                if(title.equals("Do you want to close the application?")){
////                    System.exit(0);
//
//                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
    public void disconnectFromDevice() {
//        BleManager bleManager = BleManager.getInstance();
        if (BleManager.getInstance().isConnected(bleDevice)) {
//            String deviceAddress = bleDevice.getDevice().getAddress();
            BleManager.getInstance().disconnect(bleDevice);
            BleManager.getInstance().disconnectAllDevice();

            boolean val = BleManager.getInstance().isConnected(bleDevice);
            Log.i("Ble connection",val+" ");


//            BluetoothDevice mBluetoothDevice = bleDevice.getDevice();
//            mBluetoothDevice.

            BleActivities.mBleDevice = null;

//            Intent i = new Intent(getApplicationContext(),BluetoothConnection.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            sharedPreferences.edit().putString("previousBluetoothDeviceAddress","").apply();
//            startActivity(i);
//            finish();
        }
    }
    private void updateCalorie() {

        gender = genderValue;

        float dis = currDistance - prevDistance;
        // finding time difference

//        timeStampDiffCal = timeStampCalUpd - timeStampCalUpDisTake;
//        timeStampDiffCal = Math.abs(timeStampDiffCal);
//        timeStampDiffCal = timeStampDiffCal/1000;


//        float speed = dis / 30;
        timeStampDiffCal = 30;

        prevDistance = currDistance;
        timeStampCalUpd = System.currentTimeMillis();


        float speed = dis / timeStampDiffCal;
        Log.i("READ_Prakhar_Activity","Time diff : "+timeStampDiffCal);
        float kmph = speed * 3.6f;
//        kmph = kmph / 5;
        float calorieBurn = 0;
        if (dis != 0) {
            if (steps2Sec < 4) {
                // calculating met
                calorieBurn = (float) Math.pow(1.3094 * 2.71828, 0.3548 * (kmph / 1.6));
            } else {
                // calculating met
                calorieBurn = (float) (0.0511 * (kmph / 1.61) * (kmph / 1.61) + 0.6225 * (kmph / 1.61) + 3.6191);
            }


            // now we need to calculate RMR
            double heightCM = heightM * 100;
            double weightKg = massKg;
            int ageY = age;
            double RMR_M = 0, RMR_F = 0, RMR_O = 0;

            float actualCalorieBurnM = 0, actualCalorieBurnF = 0, actualCalorieBurnO = 0;
            float actualCalorieBurn;

            if (gender.equals("Male")) {
//            Men: BMR = 88.362 + (13.397 x weight in kg) + (4.799 x height in cm) - (5.677 x age in years)
                RMR_M = 88.362 + (13.397 * massKg) + (4.799 * heightCM) - (5.677 * ageY);

                // actual calorie burn ' =CalBurn*3.5/((RMR/1440/5)/WtKg*1000)
                actualCalorieBurnM = (float) (calorieBurn * 3.5 / ((RMR_M / 1440 / 5) / massKg * 1000));
                actualCalorieBurn = actualCalorieBurnM;
            } else if (gender.equals("Female")) {
//            Women: BMR = 447.593 + (9.247 x weight in kg) + (3.098 x height in cm) - (4.330 x age in years)
                RMR_F = 447.593 + (9.247 * massKg) + (3.098 * heightCM) - (4.330 * ageY);

                // actual calorie burn '=CalBurn*2.975/((RMR/1440/5)/WtKg*1000)
                actualCalorieBurnF = (float) (calorieBurn * 2.975 / ((RMR_F / 1440 / 5) / massKg * 1000));
                actualCalorieBurn = actualCalorieBurnF;
            } else {
                RMR_O = RMR_M + RMR_F;
                RMR_O = RMR_O / 2;
                actualCalorieBurnO = actualCalorieBurnM + actualCalorieBurnF;
                actualCalorieBurnO = actualCalorieBurnO / 2;
                actualCalorieBurn = actualCalorieBurnO;
            }


            totalCalorieBurn = totalCalorieBurn + actualCalorieBurn;

            Log.i("READ_Prakhar_ActivityC", "Total calorie : " + String.valueOf(totalCalorieBurn));

//            txtDisplayCalorie.setText(String.valueOf((int) totalCalorieBurn));
            txtDisplayCalorie.setText(String.format("%.2f", totalCalorieBurn));


            // Todays data updating
//        totalTodayCalorie = (int) totalCalorieBurn;
        }
        // Todays data updating finished
    }
    public void goBack(View view) {
        BleActivities.measureHRV = false;
        finish();
    }

}