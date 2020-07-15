package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.gamebois.amaaze.graphics.GraphicSurface;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private String LOG_TAG = GameActivity.class.getSimpleName();


    GraphicSurface gs;

    private int accelerometerSensor;
    private int magneticSensor;
    private SensorManager sensorManager;

    private float[] InR = new float[16];
    private float[] I = new float[16];
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];
    private float[] orientVals = new float[3];
    private float azimuth = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;

    ArrayList<ArrayList<PointF>> rigidsurfaces = new ArrayList<>();
    ArrayList<PointF> ballPoints = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRigidSurfaces();  //just for now
        ballPoints.add(new PointF(500, 500));
        ballPoints.add(new PointF(5, 5));



        final FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(layout);

        gs = new GraphicSurface(this); // create graphic surface
        gs.getHolder().setFormat( PixelFormat.TRANSPARENT); //set graphic surface to transparent
        gs.setZOrderOnTop(true); //graphic surface as top layer
        gs.setLayoutParams(new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(gs);

        gs.setMazeArrayList(rigidsurfaces);//FEED HERE DATA);
        gs.setBallArrayList(ballPoints);//FEED HERE DATA);
        gs.setScreenSize(layout.getWidth(), layout.getHeight());  // or getMeasured???? TODO log to check if same as CamCapture

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER; //accelerometer
        magneticSensor = Sensor.TYPE_MAGNETIC_FIELD; //magnetic sensor
        
        startGame();

    }
    
    final SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged (Sensor senor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                gravity = sensorEvent.values;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomag = sensorEvent.values;
            if(gravity==null)
                Log.d(LOG_TAG,"No accelerometer values");
            if(geomag==null)
                Log.d(LOG_TAG,"No magnetic field");


            if (gravity != null && geomag != null) {
                boolean success = SensorManager.getRotationMatrix(InR, I, gravity, geomag);
                if (success) {
                    SensorManager.getOrientation(InR, orientVals); //get orientation values
                    azimuth = (float) Math.toDegrees(orientVals[0]);
                    pitch = (float) Math.toDegrees(orientVals[1]);
                    roll = (float) Math.toDegrees(orientVals[2]);
                    gs.setAzimuth(azimuth);
                    gs.setPitch(pitch);
                    gs.setRoll(roll);
                    Log.d(LOG_TAG,"azimuth: "+ String.valueOf(azimuth)+" pitch: "+String.valueOf(pitch)+" roll: "+String.valueOf(roll));

                }
            }
        }
    };

    private void setRigidSurfaces() {
        Bundle bundle = getIntent().getExtras();
        int size = bundle.getInt("size");
        for (int i = 0; i < size; i++) {
            rigidsurfaces.add(bundle.<PointF>getParcelableArrayList("item" + i));
        }
        String s = "";
        for (PointF p : rigidsurfaces.get(0)) {
            s += p.toString();
        }
        Log.d(LOG_TAG, "Number of Contours (from bundle): " + rigidsurfaces.size() + "n" + rigidsurfaces.get(0).size() + s);

    }

    private void startGame() {
        if(sensorManager != null) { //register accelerometer and  magnetometer sensors to find orientations
            sensorManager.unregisterListener(sensorEventListener);
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(magneticSensor), SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(gs.getBallArrayList()!= null && gs.getMazeArrayList() != null) {   // random checks
            gs.startGraphics();
        }
        else {
            recreate();
        }
    }


}
