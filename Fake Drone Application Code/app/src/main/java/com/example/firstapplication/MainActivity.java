package com.example.firstapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    Button startBtn, stopBtn;
    TextView battery, positionPitch, positionRoll, positionYaw , vibrationX, vibrationY, vibrationZ, controlPitch, controlRoll, controlYaw, locationX, locationY, locationZ, mod, ekfStatus, task;

    private static CSEBase csebase = new CSEBase();
    private String ServiceAEName = "CDP5";
    private ParseElementXml par = null;
    private String Mobius_Address ="13.209.165.214";
    public Handler handler;
    private autoreload ar;
    public MainActivity() {
        handler = new Handler();
        par = new ParseElementXml();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.startButton);
        stopBtn = (Button) findViewById(R.id.stopButton);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

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
        csebase.setInfo(Mobius_Address,"7579","Mobius","1883");
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startButton:{
                ar = new autoreload();
                ar.start();
                break;  
            }
            case R.id.stopButton:{
                ar.interrupt();
                break;
            }
        }
    }

     @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onStop() {
        super.onStop();

    }

    public interface IReceived {
        void getResponseBody(String msg);
    }

    class autoreload extends Thread{
        public void run(){
            try {
                while(true)
                {
                    RetrieveRequest bat = new RetrieveRequest("battery");
                    RetrieveRequest con = new RetrieveRequest("control");
                    RetrieveRequest loc = new RetrieveRequest("location");
                    RetrieveRequest tas = new RetrieveRequest("task");
                    RetrieveRequest EKF = new RetrieveRequest("EKF");
                    RetrieveRequest vib = new RetrieveRequest("vibration");
                    RetrieveRequest pos = new RetrieveRequest("position");
                    RetrieveRequest mode = new RetrieveRequest("flightmode");
                    bat.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    battery.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"battery"))));
                                }
                            });
                        }
                    });

                    con.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    controlPitch.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"Pitch"))));
                                    controlRoll.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"Roll"))));
                                    controlYaw.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"Yaw"))));
                                }
                            });
                        }
                    });

                    loc.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    locationX.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"x"))));
                                    locationY.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"y"))));
                                    locationZ.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"z"))));
                                }
                            });
                        }
                    });

                    tas.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    task.setText(par.GetElementXml(msg,"task"));
                                }
                            });
                        }
                    });

                    EKF.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    ekfStatus.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"information"))));
                                }
                            });
                        }
                    });

                    vib.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    vibrationX.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"x_vibration"))));
                                    vibrationY.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"y_vibration"))));
                                    vibrationZ.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"z_vibration"))));
                                }
                            });
                        }
                    });

                    pos.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    positionPitch.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"Pitch"))));
                                    positionRoll.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"Roll"))));
                                    positionYaw.setText(String.format("%.1f", Double.parseDouble(par.GetElementXml(msg,"Yaw"))));
                                }
                            });
                        }
                    });

                    mode.setReceiver(new IReceived() {
                        public void getResponseBody(final String msg) {
                            handler.post(new Runnable() {
                                public void run() {
                                    mod.setText(par.GetElementXml(msg,"mode"));
                                }
                            });
                        }
                    });

                    bat.start();
                    con.start();
                    loc.start();
                    tas.start();
                    EKF.start();
                    vib.start();
                    pos.start();
                    mode.start();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveRequest extends Thread {
        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());
        private IReceived receiver;
        private String ContainerName;

        public RetrieveRequest(String containerName) {
            this.ContainerName = containerName;
        }
        public RetrieveRequest() {}
        public void setReceiver(IReceived hanlder) { this.receiver = hanlder; }

        @Override
        public void run() {
            try {
                String sb = csebase.getServiceUrl() + "/" + ServiceAEName + "/" + ContainerName + "/" + "latest";

                URL mUrl = new URL(sb);

                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(false);

                conn.setRequestProperty("Accept", "application/xml");
                conn.setRequestProperty("X-M2M-RI", "12345");
                conn.setRequestProperty("X-M2M-Origin", "SCDP5" );
                conn.setRequestProperty("nmtype", "long");
                conn.connect();

                String strResp = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String strLine= "";
                while ((strLine = in.readLine()) != null) {
                    strResp += strLine;
                }

                if ( strResp != "" ) {
                    receiver.getResponseBody(strResp);
                }
                conn.disconnect();

            } catch (Exception exp) {
                LOG.log(Level.WARNING, exp.getMessage());
            }
        }
    }
}