package com.gamebois.amaaze.viewmodel;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.pathfinding.PathFinder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SolveActivityViewModel extends ViewModel {
    public static final String LOG_TAG = SolveActivityViewModel.class.getSimpleName();
    public MutableLiveData<PathFinder> pathFinder = new MutableLiveData<>();
    protected List<ContourList> rigidsurfaces = new ArrayList<>();
    public MutableLiveData<Boolean> areSurfacesRetrieved = new MutableLiveData<>(false);
    protected String ID;
    protected PointF startPoint;
    protected PointF endPoint;
    protected float radius;
    protected float height;
    protected float width;
    protected float screenWidth;
    protected float screenHeight;
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
                        setPathFinder();
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

    public void setScreenHeight(float height) {
        this.screenHeight = height;
    }

    public void setScreenWidth(float width) {
        this.screenWidth = width;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setPathFinder() {
        MazeSolverRunnable runnable = new MazeSolverRunnable();
        new Thread(runnable).start();
    }

    public LiveData<PathFinder> getPathFinder() {
        return pathFinder;
    }

    public LiveData<List<Path>> getPaths() {
        return pathLiveData;
    }

    class MazeSolverRunnable implements Runnable {

        float scale;
        float xOffset;
        float yOffset;
        PathFinder pf;

        public MazeSolverRunnable() {
            pf = new PathFinder(Math.round(height),
                    Math.round(width),
                    startPoint,
                    endPoint);
            scale = Math.min(screenWidth / width, screenHeight / height);
            xOffset = (float) ((screenWidth - width * scale) / 2.0);
            yOffset = (float) ((screenHeight - height * scale) / 2.0);
        }


        @Override
        public void run() {
            try {
                getPathsFromSurfaces(rigidsurfaces);
                pf.setUpGraph();
                pathFinder.postValue(pf);
            } catch (Throwable t) {
                Log.d("MazeRunnable", "Error generating paths");
            }
        }

        private void getPathsFromSurfaces(List<ContourList> rigidSurfaces) {
            final ArrayList<Path> paths = new ArrayList<>();
            for (ContourList surface : rigidSurfaces) {
                List<PointF> polyPoints = surface.getContourList();
                PointF firstPoint = polyPoints.get(0);
                shift(firstPoint);
                addToGrid(firstPoint);
                Path wallPath = new Path();
                wallPath.moveTo(polyPoints.get(0).x, polyPoints.get(0).y);
                for (int j = 1; j < polyPoints.size(); j++) {
                    PointF p = polyPoints.get(j);
                    shift(p);
                    addToGrid(p);
                    wallPath.lineTo(p.x, p.y);
                }
                wallPath.close();
                paths.add(wallPath);
            }
            pathLiveData.postValue(paths);
        }

        private void shift(PointF point) {
            point.set(point.x * scale + xOffset, point.y * scale + yOffset);
        }

        private void addToGrid(PointF point) {
            int xArrPos = pf.getXPosAt(Math.round(point.x));
            int yArrPos = pf.getYPosAt(Math.round(point.y));
            if (pf.nodes[xArrPos][yArrPos] == null) {
                pf.createNodeAt(xArrPos, yArrPos);
            }
            pf.nodes[xArrPos][yArrPos].setObstacle(true);
        }
    }

}



