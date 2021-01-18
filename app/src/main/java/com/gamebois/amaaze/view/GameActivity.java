package com.gamebois.amaaze.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.graphics.GraphicSurface;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.view.highscore.EndGameActivity;
import com.gamebois.amaaze.view.viewmaze.ViewMazeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    public static final String TIME_TEXT = "Chronometer";
    public static final String MAZE_ID = "MazeID";
    private String LOG_TAG = GameActivity.class.getSimpleName();

    FrameLayout layout;
    GraphicSurface gs;
    CustomChronometer chronometer;


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

    private String ID;

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
                    Log.d(LOG_TAG, "azimuth: " + azimuth + " pitch: " + pitch + " roll: " + roll);

                }
            }
        }
    };

    List<ContourList> rigidsurfaces = new ArrayList<>();
    ArrayList<PointF> wormholes = new ArrayList<>();
    private List<Float> startPoint;
    private List<Float> endPoint;
    private float radius;
    private float height;
    private float width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        ID = b.getString(ViewMazeFragment.MAZE_ID_TAG);
        startPoint = (List<Float>) b.getSerializable(ViewMazeFragment.MAZE_START_POINT);
        endPoint = (List<Float>) b.getSerializable(ViewMazeFragment.MAZE_END_POINT);
        radius = b.getFloat(ViewMazeFragment.MAZE_RADIUS);
        height = b.getFloat(ViewMazeFragment.CREATOR_HEIGHT);
        width = b.getFloat(ViewMazeFragment.CREATOR_WIDTH);
        wormholes = b.getParcelableArrayList(ViewMazeFragment.MAZE_WORMHOLE);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(getResources().getColor(R.color.background));
        setContentView(layout);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER; //accelerometer
        magneticSensor = Sensor.TYPE_MAGNETIC_FIELD; //magnetic sensor

        gs = new GraphicSurface(this); // create graphic surface
        gs.getHolder().setFormat(PixelFormat.TRANSPARENT); //set graphic surface to transparent
        gs.setZOrderOnTop(true); //graphic surface as top layer
        gs.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        gs.setStartPoint(new PointF(startPoint.get(0), startPoint.get(1)));
        gs.setEndPoint(new PointF(endPoint.get(0), endPoint.get(1)));
        gs.setEndPointRadius(radius);
        gs.setWormholesArrayList(wormholes);
        gs.setCreatorWidth(width);
        gs.setCreatorHeight(height);
        setRigidSurfaces(ID);


        layout.addView(gs);
        gs.getGameOver().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean gameOver) {
                if (gameOver) {
                    GameActivity.this.launchResultActivity();
                }
            }
        });

        chronometer = new CustomChronometer(this); // create Chronometer
        chronometer.setTextSize(20);
        chronometer.setTextColor(getResources().getColor(R.color.colorSecondary));
        FrameLayout.LayoutParams lp_chronometer = new FrameLayout.LayoutParams(
                500, // Width in pixel
                500, // Height in pixel
                Gravity.LEFT | Gravity.TOP);
        lp_chronometer.setMargins(10, 30, 10, 10);
        chronometer.setLayoutParams(lp_chronometer);
        layout.addView(chronometer);
        gs.setCustChrono(chronometer);
        
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    private void launchResultActivity() {
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra(TIME_TEXT, chronometer.getText());
        intent.putExtra(MAZE_ID, ID);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(sensorEventListener);
        if(gs!=null) {
            gs.surfaceDestroyed(gs.holder);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
      //  launchResultActivity();
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
        if(gs!=null) {
            gs.surfaceDestroyed(gs.holder);
        }
        finish();
    }

    private void setRigidSurfaces(String mazeID) {
        Task<QuerySnapshot> retrievalTask = FirebaseFirestore.getInstance()
                .collection("mazes")
                .document(mazeID)
                .collection("contours")
                .get();
        retrievalTask.addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        rigidsurfaces = documentSnapshots.toObjects(ContourList.class);
                        gs.setMazeArrayList(rigidsurfaces);
                        startGame();
                        Log.d(LOG_TAG, "rigid" + rigidsurfaces.toString());
                    }
                });
    }

    private void startGame() {
        if(sensorManager != null) { //register accelerometer and  magnetometer sensors to find orientations
            sensorManager.unregisterListener(sensorEventListener);
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(magneticSensor), SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(gs.getMazeArrayList() != null) {   // random checks
            gs.startGraphics();
        }
        else {
            recreate();
        }
        
    }


}
