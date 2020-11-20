package com.example.firstapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button startBtn, stopBtn;
    TextView battery, positionPitch, positionRoll, positionYaw , vibrationX, vibrationY, vibrationZ, controlPitch, controlRoll, controlYaw, locationX, locationY, locationZ, mod, ekfStatus, task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.startButton);
        stopBtn = (Button) findViewById(R.id.stopButton);

        battery = (TextView) findViewById(R.id.batteryInfo);

        positionPitch = (TextView) findViewById(R.id.pitchPosition);
        positionRoll = (TextView) findViewById(R.id.rollPosition);
        positionYaw = (TextView) findViewById(R.id.yawPosition);

        vibrationX = (TextView) findViewById(R.id.xVibration);
        vibrationY = (TextView) findViewById(R.id.yVibration);
        vibrationZ = (TextView) findViewById(R.id.zVibration);

        controlPitch = (TextView) findViewById(R.id.pitchControl);
        controlRoll = (TextView) findViewById(R.id.rollControl);
        controlYaw = (TextView) findViewById(R.id.yawControl);

        locationX = (TextView) findViewById(R.id.xLocation);
        locationY = (TextView) findViewById(R.id.yLocation);
        locationZ = (TextView) findViewById(R.id.zLocation);

        mod = (TextView) findViewById(R.id.flightMode);

        ekfStatus = (TextView) findViewById(R.id.efkInfo);
        task = (TextView) findViewById(R.id.missionInfo);


    }
}