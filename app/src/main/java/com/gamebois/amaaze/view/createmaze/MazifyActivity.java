package com.gamebois.amaaze.view.createmaze;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}

/*

public void saveMazeDetails() {
        Maze maze = new Maze();
        db.collection("mazes")
                .add(maze)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        executeBatchWrite(documentReference.getId());
                    }

                    private void executeBatchWrite(final String id) {
                        WriteBatch batch = db.batch();
                        CollectionReference contours = db
                                .collection("mazes")
                                .document(id)
                                .collection("contours");
                        for (ContourList contourInstance : MazifyActivity.this.rigidsurfaces) {
                            DocumentReference newDoc = contours.document();
                            batch.set(newDoc, contourInstance, SetOptions.merge());
                        }
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                launchSetBallActivityWithIntent(id);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(LOG_TAG, Objects.requireNonNull(e.getMessage()));
                                    }
                                });
                        Log.d(LOG_TAG, "Contours set");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "Uploading empty maze to Firestore failed");
                    }
                });
    }

 */