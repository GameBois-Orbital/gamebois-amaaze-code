package com.gamebois.amaaze;

import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.viewmodel.SetBallViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SetBallActivity extends AppCompatActivity {
    public static final String ID_TAG = "Maze ID";
    public static final String POINT_LIST_TAG = "Point List";

    private String LOG_TAG = SetBallActivity.class.getSimpleName();
    private SetBallViewModel mViewModel;
    //    private WorkManager workManager;
    private DrawMazeView drawMazeView;
    private String mazeID;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
        /*mViewModel = new ViewModelProvider(this).get(SetBallViewModel.class);
        mazeID = getIntent().getStringExtra(CameraCaptureActivity.ID_TAG);
        drawMazeView = new DrawMazeView(this);
        if (mViewModel.checkIfPathsAreSet()) {
            configureMazeView();
        } else {
            setContentView(R.layout.activity_loading);
            startWork();
        }
    }

    private void startWork() {
        PathGeneratorRunnable runnable = new PathGeneratorRunnable();
        new Thread(runnable).start();
    }

    private void configureMazeView() {
        drawMazeView.setPaths(mViewModel.getPaths());
        drawMazeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(drawMazeView);
    }

    class PathGeneratorRunnable implements Runnable {

        public PathGeneratorRunnable() {
        }

        @Override
        public void run() {
            DocumentReference maze = FirebaseFirestore.getInstance()
                    .collection("mazes")
                    .document(mazeID);
            try {
                Task<QuerySnapshot> retrievalTask = maze.collection("contours")
                        .get();
                retrievalTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        getPathsFromSurfaces(documentSnapshots.toObjects(ContourList.class));
                    }
                });
            } catch (Throwable t) {
                Log.d(LOG_TAG, "Error generating paths");
            }
        }

        private void getPathsFromSurfaces(List<ContourList> rigidSurfaces) {
            final ArrayList<Path> paths = new ArrayList<>();
            for (ContourList surface : rigidSurfaces) {
                List<PointF> polyPoints = surface.getContourList();
                Path wallPath = new Path();
                wallPath.moveTo(polyPoints.get(0).x, polyPoints.get(0).y);
                for (int j = 1; j < polyPoints.size(); j++) {
                    PointF p = polyPoints.get(j);
                    wallPath.lineTo(p.x, p.y);
                }
                wallPath.close();
                paths.add(wallPath);
                Log.d(LOG_TAG, paths.toString());
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mViewModel.setPaths(paths);
                    Log.d(LOG_TAG, paths.toString());
                    configureMazeView();
                }
            });
        }
    }
}

    /*
    int size = bundle.getInt("size");
        for (int i = 0; i < size; i++) {
            rigidsurfaces.add(bundle.<PointF>getParcelableArrayList("item" + i));
        }
        String s = "";
        for (PointF p : rigidsurfaces.get(0)) {
            s += p.toString();
        }
        Log.d(LOG_TAG, "Number of Contours (from bundle): " + rigidsurfaces.size() + "n" + rigidsurfaces.get(0).size() + s);

     */

    /*
    final String mazeID = getIntent().getStringExtra(CameraCaptureActivity.ID_TAG);
        Data data = new Data.Builder()
                .putString(ID_TAG, mazeID)
                .build();
        final OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(PathGenerator.class)
                .setInputData(data)
                .build();
        workManager = WorkManager.getInstance(this);
        workManager.enqueue(request);
        workManager.getWorkInfoByIdLiveData(request.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                    }
                });         */
