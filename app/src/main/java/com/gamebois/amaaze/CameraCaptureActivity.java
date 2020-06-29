package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
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

public class CameraCaptureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    //int mat_width = 1280;
   // int mat_height = 960;


    private static String TAG = "OpenCVCamera";
    private int CAMERA_REQ = 10;
    CameraBridgeViewBase cameraBridgeviewbase;
    Mat frame;
    Mat filtered;
    Mat processed;
    List<MatOfPoint> contours = new ArrayList<>();
    //DetectMaze detectMaze;
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
        contours.clear();
        frame = inputFrame.rgba();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(frame, processed, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.bilateralFilter(processed, filtered, 3, 170, 3);
        Imgproc.adaptiveThreshold(filtered, processed, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 7); // apply adaptive thresolding to blured frame
        Imgproc.dilate(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 17), new Point(0, 0)));
        Imgproc.erode(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 17), new Point(0, 0)));
        Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxVal = 0;
        int max_index = -1;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal < contourArea) {
                maxVal = contourArea;
                max_index = contourIdx;
            }
        }
        if( max_index >=0) {
            Imgproc.drawContours(frame, contours, max_index, new Scalar(0, 255, 0), 2, Core.LINE_8, hierarchy, 0, new Point());
        }
        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat();
        processed = new Mat();
        filtered = new Mat();

    }


    @Override
    public void onCameraViewStopped() {
        frame.release();
        processed.release();
        filtered.release();
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


}

