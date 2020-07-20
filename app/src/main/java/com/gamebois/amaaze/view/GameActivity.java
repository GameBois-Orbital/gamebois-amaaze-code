package com.gamebois.amaaze.view;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.graphics.GraphicSurface;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.Maze;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.graph.Graph;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    String ID;

    float layoutWidth, layoutHeight;


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
    ArrayList<PointF> ballPoints = new ArrayList<>();
    List<ContourList> rigidsurfaces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getIntent().getStringExtra(ViewMazeFragment.MAZE_ID_TAG);

        ballPoints.add(new PointF(500, 500));
        ballPoints.add(new PointF(5, 5));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_game);
        final FrameLayout layout = findViewById(R.id.my_frameLayout);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER; //accelerometer
        magneticSensor = Sensor.TYPE_MAGNETIC_FIELD; //magnetic sensor

        gs = new GraphicSurface(this); // create graphic surface
        gs.getHolder().setFormat(PixelFormat.TRANSPARENT); //set graphic surface to transparent
        gs.setZOrderOnTop(true); //graphic surface as top layer
        gs.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        gs.setBallArrayList(ballPoints);//FEED HERE DATA);
        setRigidSurfaces(ID);
        layout.addView(gs);

    }


    private void setRigidSurfaces(String mazeID) {
        final DocumentReference maze = FirebaseFirestore.getInstance()
                .collection("mazes")
                .document(mazeID);
        Task<DocumentSnapshot> getTask = maze.get();
        Task<QuerySnapshot> retrievalTask = getTask.continueWithTask(new Continuation<DocumentSnapshot, Task<QuerySnapshot>>() {
            @Override
            public Task<QuerySnapshot> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                Maze m = task.getResult().toObject(Maze.class);
                gs.setCreatorHeight(m.getHeight());
                gs.setCreatorWidth(m.getWidth());
                return maze.collection("contours")
                        .get();
            }
        });
        retrievalTask.addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        rigidsurfaces = documentSnapshots.toObjects(ContourList.class);
                        gs.setMazeArrayList(rigidsurfaces);
                        startGame();
                    }
                });
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
