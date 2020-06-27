package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraCaptureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCVCamera";
    CameraBridgeViewBase cameraBridgeViewBase;
    Mat frame;
    //Mat processed;
    Mat filtered;
    //List<MatOfPoint> contours;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == BaseLoaderCallback.SUCCESS) {
                cameraBridgeViewBase.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.my_camera_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(frame, frame, 200, 80);
        Imgproc.bilateralFilter(frame, filtered, 5, 170, 5);
        /*

        Imgproc.dilate(filtered, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15), new Point(0,0)));
        Imgproc.erode(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15), new Point(0,0)));

        Imgproc.findContours(processed, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(frame, contours,-1, new Scalar( 0, 255, 0));
         */
        //Imgproc.GaussianBlur(frame, frame, new Size(7,7), 2, 2); // perform gaussian blur to remove noise
        //Imgproc.adaptiveThreshold(filtered, frame,255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV,3, 3); // apply adaptive thresolding to blured frame
        Imgproc.dilate(filtered, frame, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9), new Point(-1, -1))); // dilate the frame to fill gaps
        Imgproc.erode(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9), new Point(-1, -1)));
        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat(height, width, CvType.CV_8UC4);
        //processed = new Mat(height, width, CvType.CV_8UC1);
        filtered = new Mat(height, width, CvType.CV_8UC1);
        //contours = new ArrayList<>();

    }


    @Override
    public void onCameraViewStopped() {
        frame.release();
        //filtered.release();
        //filtered.release();
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
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }


}

