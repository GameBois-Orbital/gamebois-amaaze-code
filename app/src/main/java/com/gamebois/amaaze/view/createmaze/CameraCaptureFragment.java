package com.gamebois.amaaze.view.createmaze;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.viewmodel.MazifyActivityViewModel;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;


public class  CameraCaptureFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {

    public static final String ID_TAG = "Maze ID";
    private static String TAG = "OpenCVCamera";
    public Mat frame;
    CameraBridgeViewBase mOpenCVCameraView;
    DetectMaze dm;
    Button setButton;
    ArrayList<ContourList> rigidSurfaces;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == BaseLoaderCallback.SUCCESS) {
                mOpenCVCameraView.enableView();
//                mOpenCVCameraView.setCvCameraViewListener(CameraCaptureFragment.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };
    private MazifyActivityViewModel mViewModel;

    public CameraCaptureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Loaded");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV Not Loading or Unsuccessful");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, getActivity(), mLoaderCallBack);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera_capture, container, false);
        mOpenCVCameraView = view.findViewById(R.id.my_camera_view);
        mOpenCVCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCVCameraView.setCvCameraViewListener(this);
        setButton = view.findViewById(R.id.set_button);
        setButton.setOnClickListener(this);
        mViewModel = new ViewModelProvider(requireActivity()).get(MazifyActivityViewModel.class);
        return view;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();
        dm = new DetectMaze(mOpenCVCameraView.getWidth(), mOpenCVCameraView.getHeight(), mOpenCVCameraView.getMatWidth(), mOpenCVCameraView.getMatHeight(), mOpenCVCameraView.getmScale());
        Log.d(TAG, "screen: "+ mOpenCVCameraView.getWidth() + "x" + mOpenCVCameraView.getHeight() +
                "Mat: " + mOpenCVCameraView.getMatWidth() + "x" + mOpenCVCameraView.getMatHeight() + "Scale:" + mOpenCVCameraView.getmScale());
        dm.process(frame);
        rigidSurfaces = dm.getRigidSurfaces();
//        Log.d(LOG_TAG, "Number of Contours (in rigidSurfaces)" + Integer.toString(rigidsurfaces.size()));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_button:
                setButton.setEnabled(false);
                mViewModel.setCreatorHeight(mOpenCVCameraView.getHeight());
                mViewModel.setCreatorWidth(mOpenCVCameraView.getWidth());
                mViewModel.setMat(frame);
                mOpenCVCameraView.disableView();
                mViewModel.setRigidSurfaces(this.rigidSurfaces);
                Navigation.findNavController(v).navigate(R.id.action_cameraCaptureFragment_to_setBallFragment);
        }
    }
}
