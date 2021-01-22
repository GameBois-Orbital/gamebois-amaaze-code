package com.gamebois.amaaze.viewmodel;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.imageprocessing.PathGeneratorRunnable;
import com.gamebois.amaaze.model.pathfinding.MazeSolverRunnable;
import com.gamebois.amaaze.model.pathfinding.Node;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SolveActivityViewModel extends ViewModel {
    public static final String LOG_TAG = SolveActivityViewModel.class.getSimpleName();
    public MutableLiveData<Node[]> gridLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> areSurfacesRetrieved = new MutableLiveData<>(false);
    List<ContourList> rigidsurfaces = new ArrayList<>();
    private String ID;
    private PointF startPoint;
    private PointF endPoint;
    private float radius;
    private float height;
    private float width;
    private MutableLiveData<List<Path>> pathLiveData = new MutableLiveData<>();

    public List<ContourList> getRigidsurfaces() {
        return rigidsurfaces;
    }

    public void setRigidsurfaces() {
        Task<QuerySnapshot> retrievalTask = FirebaseFirestore.getInstance()
                .collection("mazes")
                .document(ID)
                .collection("contours")
                .get();
        retrievalTask.addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        rigidsurfaces = documentSnapshots.toObjects(ContourList.class);
                        areSurfacesRetrieved.postValue(true);
                        Log.d(LOG_TAG, "rigid" + rigidsurfaces.toString());
                    }
                });
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public PointF getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PointF startPoint) {
        this.startPoint = startPoint;
    }

    public PointF getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PointF endPoint) {
        this.endPoint = endPoint;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setPaths() {
        PathGeneratorRunnable pathRunnable = new PathGeneratorRunnable(rigidsurfaces, pathLiveData);
        new Thread(pathRunnable).start();
    }

    public void setGrid() {
        MazeSolverRunnable mazeRunnable = new MazeSolverRunnable(height, width, pathLiveData,
                startPoint, endPoint, gridLiveData);
        new Thread(mazeRunnable).start();
    }

    public LiveData<Node[]> getGrid() {
        return gridLiveData;
    }

    public LiveData<List<Path>> getPaths() {
        return pathLiveData;
    }

}

//    class ConcurrentRunnable implements Runnable {
//
//        private final Runnable first;
//        private final Runnable second;
//
//        public ConcurrentRunnable(Runnable first, Runnable second)
//        {
//            this.first = first;
//            this.second = second;
//        }
//
//        @Override
//        public void run()
//        {
//            first.run();
//            second.run();
//        }
//    }
