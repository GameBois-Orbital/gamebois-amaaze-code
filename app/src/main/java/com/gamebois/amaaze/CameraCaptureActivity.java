package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CameraCaptureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
   // public static final String EXTRA_MAZE_LIST = "com.gamebois.ammaze.EXTRA_MAZE_LIST";

    int mat_width = 1280;
    int mat_height = 960;


    private static String TAG = "OpenCVCamera";
    private int CAMERA_REQ = 10;
    CameraBridgeViewBase cameraBridgeviewbase;
    public Mat frame;

    DetectMaze dm;
    ArrayList<PointF> androidPts = new ArrayList<PointF>();

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == BaseLoaderCallback.SUCCESS){
                cameraBridgeviewbase.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private String LOG_TAG = CameraCaptureActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_capture);
        cameraBridgeviewbase = (JavaCameraView) findViewById(R.id.my_camera_view);
        cameraBridgeviewbase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeviewbase.setCvCameraViewListener(this);

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();
        dm = new DetectMaze(1920, 1080, 1920, 720);
        dm.process(frame);
        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat();

    }


    @Override
    public void onCameraViewStopped() {
        frame.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Loaded");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV Not Loading or Unsuccessful");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallBack);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeviewbase != null) {
            cameraBridgeviewbase.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeviewbase != null) {
            cameraBridgeviewbase.disableView();
        }
    }



    public void launchSetBallActivity(View view) {
        cameraBridgeviewbase.disableView();
        androidPts = dm.getMazePoints();
        Log.d(LOG_TAG, "Set Clicked");
        Intent intent = new Intent(this, SetBallActivity.class);
        intent.putParcelableArrayListExtra("maze", androidPts);
        startActivity(intent);
    }
}

