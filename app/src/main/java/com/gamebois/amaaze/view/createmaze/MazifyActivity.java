package com.gamebois.amaaze.view.createmaze;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gamebois.amaaze.R;

import org.opencv.android.CameraBridgeViewBase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;

public class MazifyActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private String LOG_TAG = MazifyActivity.class.getSimpleName();

    //Methods copied from OpenCV CameraActivity, in order to extend AppCompatActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return new ArrayList<CameraBridgeViewBase>();
    }

    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase : cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
                onCameraPermissionGranted();
            } else if (shouldShowRequestPermissionRationale(CAMERA)) {
                Toast.makeText(this, "Should show permissions!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }
    }

}