package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CameraCaptureActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
   // public static final String EXTRA_MAZE_LIST = "com.gamebois.ammaze.EXTRA_MAZE_LIST";

    private String LOG_TAG = CameraCaptureActivity.class.getSimpleName();
    private static String TAG = "OpenCVCamera";

    CameraBridgeViewBase mOpenCVCameraView;
    public Mat frame;
    DetectMaze dm;
    ArrayList<ArrayList<PointF>> rigidsurfaces;

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == BaseLoaderCallback.SUCCESS){
                mOpenCVCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        mOpenCVCameraView = findViewById(R.id.my_camera_view);
        mOpenCVCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCVCameraView.setCvCameraViewListener(this);
       
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();
        dm = new DetectMaze(mOpenCVCameraView.getWidth(), mOpenCVCameraView.getHeight(), mOpenCVCameraView.getMatWidth(), mOpenCVCameraView.getMatHeight(), mOpenCVCameraView.getmScale());
        dm.process(frame);
        rigidsurfaces = dm.getRigidSurfaces();
        //Log.d(LOG_TAG, "Number of Contours (in rigidSurfaces)" + Integer.toString(rigidsurfaces.size()));
        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat(height, width, CvType.CV_8UC4);
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
        if (mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }
        Log.d(LOG_TAG, "onDestroy called");
    }



    public void launchSetBallActivity(View view) {
        Log.d(LOG_TAG, "Set Clicked");
        Intent intent = new Intent(this, SetBallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("size", rigidsurfaces.size());
        for (int i = 0; i < rigidsurfaces.size(); i++) {
            bundle.putParcelableArrayList("item"+i, rigidsurfaces.get(i));
        }
        intent.putExtras(bundle);
        Log.d(LOG_TAG, "Number of Contours (to bundle): " + intent.getExtras().getInt("size"));

        startActivity(intent);
        finish();

    }
}

