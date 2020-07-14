package com.gamebois.amaaze.backgroundwork;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.gamebois.amaaze.SetBallActivity;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.PathList;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PathGenerator extends Worker {

    public static final String TAG = PathGenerator.class.getSimpleName();
    private DocumentReference maze;

    public PathGenerator(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String mazeID = getInputData().getString(SetBallActivity.ID_TAG);
        maze = FirebaseFirestore.getInstance()
                .collection("mazes")
                .document(mazeID);
        try {
            Task<Void> task = getRigidSurfaces(mazeID);
            Tasks.await(task);
            return Result.success();
        } catch (Throwable throwable) {
            Log.e(TAG, "Error generating paths", throwable);
            return Result.failure();
        }
    }

    private Task<Void> getRigidSurfaces(String mazeID) {
        Task<QuerySnapshot> retrievalTask =
                maze.collection("contours")
                        .get();
        return retrievalTask.continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                QuerySnapshot mSnapshot = task.getResult();
                return updatePaths(mSnapshot);
            }
        });
    }

    private Task<Void> updatePaths(QuerySnapshot contourSnapshot) {
        PathList mPaths = getPathsFromSurfaces(contourSnapshot.toObjects(ContourList.class));
        return maze.update("paths", mPaths);
    }

    private PathList getPathsFromSurfaces(List<ContourList> rigidSurfaces) {
        ArrayList<Path> paths = new ArrayList<>();
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
        }
        Log.d(TAG, paths.toString());
        return new PathList(paths);
    }
}
