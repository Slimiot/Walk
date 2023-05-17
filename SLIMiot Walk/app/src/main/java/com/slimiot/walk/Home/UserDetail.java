package com.slimiot.walk.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.slimiot.walk.BleAction.BluetoothConnection;
import com.slimiot.walk.BleOperations.BleActivities;
import com.slimiot.walk.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserDetail extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button nextButton;
    private MaterialAutoCompleteTextView gender,weight,height;
    private TextInputLayout genderLayout,weightLayout,heightLayout;
    private String genderValue = null , dobValue=null ;
    private SharedPreferences sharedPreferences;
    String[] spinValues;
    Spinner spinner;
    private List<String> heightParam,weightParam;
    private int heightFt,heightIn,selectedModeHeight,selectedModeWeight,selectedModeWaist,stepGoal,weight1,selectedSkinTone;
    private String weightUnit,heightUnit,waistUnit,imgUrl,step;
    private int intAbsHeight,intAbsWaist=0;
    private double absHeight=0,absWeight=0,absWaist=0,weight2;
    private ArrayAdapter<String> heightParamAdapter,weightParamAdapter;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        getSupportActionBar().hide();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        heightParam=new ArrayList<>();
        weightParam=new ArrayList<>();
        heightParam.add("Feet & Inches");
        heightParam.add("Centimeters");
        weightParam.add("Kilograms");
        weightParam.add("Pounds");
        nextButton = findViewById(R.id.nextButton);
        genderLayout=findViewById(R.id.genderLayout);
        weightLayout=findViewById(R.id.weightLayout);
        heightLayout=findViewById(R.id.heightLayout);
        gender=findViewById(R.id.genderProfile);
        weight=findViewById(R.id.weightProfile);
        height=findViewById(R.id.heightProfile);
        spinValues = new String[]{"Hr & Hrv (180D)", "Timestamp 4byte + Hr Hrv"};
        spinner = findViewById(R.id.dataFormatSpinner);
        sharedPreferences=this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        getData();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkAnalysis();
            }
        });
        genderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnGender();
            }
        });
        weightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnWeight();
            }
        });
        heightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnHeight();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnGender();
            }
        });
        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnHeight();
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnWeight();
            }
        });
        genderLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();
                workOnGender();
                // do some code
            }
        });
        weightLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();
                workOnWeight();
                // do some code
            }
        });
        heightLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();
                workOnHeight();
                // do some code
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(mContext, spinValues[i], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_item,spinValues);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
    }

    private void getData(){
        genderValue = sharedPreferences.getString("gender",null);

        if(genderValue!=null){
            gender.setText(genderValue);
        }
        weightUnit = sharedPreferences.getString("weightUnit",null);
        heightUnit = sharedPreferences.getString("heightUnit",null);
//        Log.i("screen_u",sharedPreferences.getString("weight",null));




        if(heightUnit!=null) {
            absHeight =  Double.parseDouble(sharedPreferences.getString("height",null));
            if (heightUnit.equals("Centimeters")) {
                intAbsHeight = (int) absHeight;
//                heightM=absHeight/100;
                if(intAbsHeight != 0) {
                    height.setText(intAbsHeight + " cm");
                }
                //  height.setText(heightValue+" cm");
            } else if (heightUnit.equals("Feet & Inches")){
//                heightM=absHeight*0.0254;
                int x = (int) absHeight / 12;
                int y = (int) Math.round(absHeight % 12);
                height.setText(x + "'" + y + "''");
//                            Toast.makeText(ProfileActivity.this, "Hello", Toast.LENGTH_SHORT).show();
//                            if(x!=0&&y!=0) {
//                                height.setText(x + "'" + y + "''");
//                            }
            }
        }
        if(weightUnit!=null) {
            absWeight =  Double.parseDouble(sharedPreferences.getString("weight",null));
            if (weightUnit.equals("Kilograms")) {
                if(absWeight!=0.0) {
//                    massKg = absWeight;
                    String s=new DecimalFormat("#####.#").format(absWeight);
                    weight.setText(s + " kg");
                }
            }else if(weightUnit.equals("Pounds")){
                if(absWeight!=0.0) {
//                    massKg=absWeight/2.20462;
                    String s = new DecimalFormat("#####.#").format(absWeight);
                    weight.setText(s + " lb");
                }
            }
        }


//        sharedPreferences.edit().putString("gender",genderValue).apply();
//        sharedPreferences.edit().putString("dob",dobValue).apply();
    }

    private void walkAnalysis() {
        String genderValue = gender.getText().toString();
        String weightValue = weight.getText().toString();
        String heightValue = height.getText().toString();







//        Toast.makeText(getApplicationContext(), genderValue , Toast.LENGTH_SHORT).show();

        if(genderValue==null||genderValue.equals("")){
            Toast.makeText(this, "Please fill the gender fields", Toast.LENGTH_SHORT).show();
        }else if(weightValue==null||weightValue.equals("")){
            Toast.makeText(this, "Please fill the weight fields", Toast.LENGTH_SHORT).show();
        }else if(heightValue==null||heightValue.equals("")){
            Toast.makeText(this, "Please fill the height fields", Toast.LENGTH_SHORT).show();
        }else{

            String spinValue = spinner.getSelectedItem().toString();
            if(spinValue.equals("Hr & Hrv (180D)")){
                BleActivities.k = 0;
            }else if(spinValue.equals("Timestamp 4byte + Hr Hrv")){
                BleActivities.k = 4;
            }


            sharedPreferences.edit().putString("gender",genderValue).apply();
            sharedPreferences.edit().putString("height",String.valueOf(absHeight)).apply();
            sharedPreferences.edit().putString("weight",String.valueOf(absWeight)).apply();
            sharedPreferences.edit().putString("heightUnit",heightUnit).apply();
            sharedPreferences.edit().putString("weightUnit",weightUnit).apply();
            startActivity(new Intent(getApplicationContext(), BluetoothConnection.class));
        }
    }

    private void workOnGender() {
        final AlertDialog.Builder d = new AlertDialog.Builder(UserDetail.this);
        LayoutInflater inflater = UserDetail.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_gender, null);
        d.setView(dialogView);
        final RadioButton male = dialogView.findViewById(R.id.maleRadio);
        final RadioButton female = dialogView.findViewById(R.id.femaleRadio);
        final RadioButton other = dialogView.findViewById(R.id.otherRadio);
        final TextView ok = dialogView.findViewById(R.id.okGender);
        final TextView cancel =dialogView.findViewById(R.id.cancelGender);
        final RadioGroup radioGroup = dialogView.findViewById(R.id.genderRadioGroup);
        if(genderValue!=null){
            if(genderValue.equals("Male")){
                male.setChecked(true);
            }else if(genderValue.equals("Female")){
                female.setChecked(true);
            }else if(genderValue.equals("Other")){
                other.setChecked(true);
            }
        }
        AlertDialog alertDialog = d.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(600, 400);
        alertDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                switch (selectedId){
                    case R.id.maleRadio: gender.setText("Male");
                        break;
                    case R.id.femaleRadio: gender.setText("Female");
                        break;
                    case R.id.otherRadio: gender.setText("Other");
                        break;
                }
                gender.clearFocus();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender.clearFocus();
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                genderLayout.clearFocus();
            }
        });
    }
    private void workOnWeight() {
        final AlertDialog.Builder d = new AlertDialog.Builder(UserDetail.this);
        LayoutInflater inflater = UserDetail.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_weight, null);
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.weight_number_picker);
        final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.weight_number_picker2);
        final Spinner spinnerWeightParam = (Spinner) dialogView.findViewById(R.id.spinnerWeightParam);
        final TextView ok =  dialogView.findViewById(R.id.okWeightDialog);
        final TextView cancel = dialogView.findViewById(R.id.cancelWeightDialog);
        final TextView weightDialogText=dialogView.findViewById(R.id.weightDialogText);
        final TextView weightPoint=dialogView.findViewById(R.id.weightPoint);
        weightParamAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, weightParam);
        spinnerWeightParam.setAdapter(weightParamAdapter);
        if(weightUnit!=null){
            if(weightUnit.equals("Kilograms")){
                spinnerWeightParam.setSelection(0);
            }else if(weightUnit.equals("Pounds")){
                spinnerWeightParam.setSelection(1);
            }
        }
        spinnerWeightParam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedModeWeight = spinnerWeightParam.getSelectedItemPosition();
                if(selectedModeWeight==0) {
                    numberPicker.setMaxValue(450);
                    numberPicker.setMinValue(1);
                    numberPicker2.setMaxValue(9);
                    numberPicker2.setMinValue(0);
                    weightPoint.setVisibility(View.VISIBLE);
                    weightDialogText.setVisibility(View.VISIBLE);
                    weightDialogText.setText("kg");
                    if(absWeight!=0.0) {
                        String s = new DecimalFormat("#####.#").format(absWeight);
                        String[] arr=s.split("\\.");
                        int[] intArr=new int[2];
                        intArr[0]=Integer.parseInt(arr[0]);
                        if(arr.length==2){
                            intArr[1]=Integer.parseInt(arr[1]);
                            numberPicker2.setValue(intArr[1]);
                        }else{
                            numberPicker2.setValue(0);
                        }
                        numberPicker.setValue(intArr[0]);
                    }
                    numberPicker.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return value+" ";
                        }
                    });
                    numberPicker2.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return value+" ";
                        }
                    });
                }else if(selectedModeWeight==1){
                    numberPicker.setMaxValue(1000);
                    numberPicker.setMinValue(1);
                    numberPicker2.setMaxValue(9);
                    numberPicker2.setMinValue(0);
                    weightDialogText.setVisibility(View.VISIBLE);
                    weightDialogText.setText("lb");
                    weightPoint.setVisibility(View.VISIBLE);
                    if(absWeight!=0.0) {
                        String s = new DecimalFormat("#####.#").format(absWeight);
                        String[] arr=s.split("\\.");
                        int[] intArr=new int[2];
                        intArr[0]=Integer.parseInt(arr[0]);
//                        intArr[1]=Integer.parseInt(arr[1]);
                        if(arr.length==2){
                            intArr[1]=Integer.parseInt(arr[1]);
                            numberPicker2.setValue(intArr[1]);
                        }else{
                            numberPicker2.setValue(0);
                        }
                        numberPicker.setValue(intArr[0]);
                    }
                    numberPicker.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return value+" ";
                        }
                    });
                    numberPicker2.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return value+" ";
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        weightDialogText.setText("kg");
        weightPoint.setVisibility(View.VISIBLE);
        numberPicker.setMaxValue(450);
        numberPicker.setMinValue(1);
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);
        numberPicker.setValue(60);
        numberPicker2.setValue(0);
        try {
            Method method = numberPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPicker, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            Method method = numberPicker2.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPicker2, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        AlertDialog alertDialog = d.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(600, 400);
        alertDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedModeWeight==0) {
                    weight1=numberPicker.getValue();
                    weight2=numberPicker2.getValue();
                    absWeight=weight1+(weight2/10);
                    weight.setText(weight1+"."+(int)weight2+" kg");
                    weightUnit="Kilograms";
                }else  if(selectedModeWeight==1){
//                    weight1=numberPicker.getValue();
//                    weight2=numberPicker2.getValue();
//                    absWeight=weight1*14+weight2;
//                    weight.setText(weight1+" st "+(int)weight2+" lb");
//                    weightUnit="Stones";
                    weight1=numberPicker.getValue();
                    weight2=numberPicker2.getValue();
                    absWeight=weight1+(weight2/10);
                    weight.setText(weight1+"."+(int)weight2+" lb");
                    weightUnit="Pounds";
                }else if(selectedModeWeight==2){
                    weight1=numberPicker.getValue();
                    weight2=numberPicker2.getValue();
                    absWeight=weight1+(weight2/10);
                    weight.setText(weight1+"."+(int)weight2+" lb");
                    weightUnit="Pounds";
                }
                weight.clearFocus();
                alertDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight.clearFocus();
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                weightLayout.clearFocus();
            }
        });
    }

    private void workOnHeight() {
        final AlertDialog.Builder d = new AlertDialog.Builder(UserDetail.this);
        LayoutInflater inflater = UserDetail.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_height, null);
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker2);
        final NumberPicker numberPickerCm = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker_cm);
        final Spinner spinnerHeightParam = (Spinner) dialogView.findViewById(R.id.spinnerHeightParam);
        final TextView ok =  dialogView.findViewById(R.id.okHeightDialog);
        final TextView cancel = dialogView.findViewById(R.id.cancelHeightDialog);
        heightParamAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, heightParam);
        spinnerHeightParam.setAdapter(heightParamAdapter);
        if(heightUnit!=null){
            if(heightUnit.equals("Feet & Inches")){
                spinnerHeightParam.setSelection(0);
            }else if(heightUnit.equals("Centimeters")){
                spinnerHeightParam.setSelection(1);
            }
        }
        spinnerHeightParam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedModeHeight = spinnerHeightParam.getSelectedItemPosition();
                if(selectedModeHeight==0) {
                    numberPickerCm.setVisibility(View.GONE);
                    numberPicker.setVisibility(View.VISIBLE);
                    numberPicker2.setVisibility(View.VISIBLE);
                    if(absHeight!=0) {
                        int x = (int) absHeight / 12;
                        int y = (int) Math.round(absHeight % 12);
                        if (x != 0 && y != 0) {
                            numberPicker.setValue(x);
                            numberPicker2.setValue(y);
                        }
                    }
                }else if(selectedModeHeight==1){
                    numberPickerCm.setVisibility(View.VISIBLE);
                    numberPicker.setVisibility(View.GONE);
                    numberPicker2.setVisibility(View.GONE);
                    if(absHeight!=0) {
                        intAbsHeight = (int) absHeight;
                        if (intAbsHeight != 0)
                            numberPickerCm.setValue(intAbsHeight);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        numberPicker.setMaxValue(8);
        numberPicker.setMinValue(1);
        numberPickerCm.setMaxValue(272);
        numberPickerCm.setMinValue(30);
        numberPicker2.setMaxValue(11);
        numberPicker2.setMinValue(0);
//            numberPicker.setWrapSelectorWheel(false);
//            numberPicker2.setWrapSelectorWheel(false);
//            numberPickerCm.setWrapSelectorWheel(false);
        numberPickerCm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return value + " cm";
            }
        });
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return value + " ft";
            }
        });
        numberPicker2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return value + " in";
            }
        });
        numberPicker.setValue(5);
        numberPickerCm.setValue(200);
        numberPicker2.setValue(6);
        try {
            Method method = numberPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPicker, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            Method method = numberPicker2.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPicker2, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            Method method = numberPickerCm.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPickerCm, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        AlertDialog alertDialog = d.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(600, 400);
        alertDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedModeHeight==0) {
                    heightFt = numberPicker.getValue();
                    heightIn = numberPicker2.getValue();
                    absHeight=heightFt*12;
                    absHeight=absHeight+heightIn;
                    int x =(int)absHeight/12;
                    int y =(int) Math.round(absHeight%12);
                    height.setText(x+"'"+y+"''");
                    heightUnit = "Feet & Inches";
                }else  if(selectedModeHeight==1){
                    absHeight=numberPickerCm.getValue();
                    intAbsHeight =(int) absHeight;
                    height.setText(intAbsHeight+" cm");
                    heightUnit = "Centimeters";
                }
                height.clearFocus();
                alertDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                height.clearFocus();
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                heightLayout.clearFocus();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}