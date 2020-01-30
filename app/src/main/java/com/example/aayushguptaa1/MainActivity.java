package com.example.aayushguptaa1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView textView1, textView2, textView3, textView4;
    private EditText editText1;
    private Button button1, button2;
    private SensorManager sensorManager;
    private String threshold = null;
    private File outputFile = null;
    Sensor accelerometer;
    private boolean clicked = false;
    private Date lastTime = null;
    private Date curTime = null;
    private List<String[]>  dataList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSensor();
        bindView();
        textView2.setVisibility(View.INVISIBLE);
    }
    private void createSensor()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void bindView(){
        textView1 = findViewById(R.id.textView1);
        textView2 =findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 =findViewById(R.id.textView4);
        editText1 = findViewById(R.id.editText1);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
    }
    public void onStartClick(View view)
    {
        threshold = String.valueOf(editText1.getText());
        if(threshold == null || threshold.length() == 0)
        {
            Toast.makeText(getBaseContext(), "Please enter threshold value", Toast.LENGTH_LONG).show();
        }
        else
        {
            textView2.setVisibility(View.VISIBLE);
            dataList = new ArrayList<>();
            clicked = true;
        }
    }

    public void onStopClick(View view) throws IOException
    {
        clicked = false;
        textView2.setVisibility(View.INVISIBLE);
        editText1.setText("");
        writeFile(threshold);
        threshold = "";
    }

    public void writeFile(String threshold) throws IOException
    {
        String fileName = threshold + "_output.csv";
        FileOutputStream output = openFileOutput(fileName, Context.MODE_PRIVATE);
        output.write("ax,ay,az\n".getBytes());
        for(String[] s : dataList)
        {
            output.write((s[0] + "," + s[1] + ","+ s[2] + "\n").getBytes());
        }
        output.close();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        String ax = ""+event.values[0];
        String ay = ""+event.values[1];
        String az = ""+event.values[2];
        textView2.setText("{"+ax+","+ay+","+az+"}");
        if(clicked)
        {
            if(lastTime == null)
            {
                lastTime = new Date();
                dataList.add(new String[]{ax,ay,az});
            }
            else
            {
                curTime = new Date();
                if(curTime.getTime() - lastTime.getTime() >= 1000 && dataList != null)
                {
                    dataList.add(new String[]{ax,ay,az});
                }
                lastTime = curTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

