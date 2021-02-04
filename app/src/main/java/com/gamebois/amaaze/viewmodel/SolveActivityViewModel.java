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

    //LiveData variables to be observed in view layer
    public MutableLiveData<Boolean> areSurfacesRetrieved = new MutableLiveData<>(false);
    //Maze attributes
    protected List<ContourList> rigidsurfaces = new ArrayList<>();
    protected float mazeHeight;
    protected float mazeWidth;
    private MutableLiveData<List<Path>> pathLiveData = new MutableLiveData<>();
    protected String ID;
    protected PointF startPoint;
    protected PointF endPoint;
    protected float radius;
    private MutableLiveData<PathFinder> pathFinder = new MutableLiveData<>();
    private MutableLiveData<Path> solutionLiveData = new MutableLiveData<>();
    protected float screenWidth;
    protected float screenHeight;

    //Getters and setters for maze attributes

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setStartPoint(PointF startPoint) {
        this.startPoint = startPoint;
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

    public void setMazeHeight(float mazeHeight) {
        this.mazeHeight = mazeHeight;
    }

    public void setMazeWidth(float mazeWidth) {
        this.mazeWidth = mazeWidth;
    }

    public void setScreenHeight(float height) {
        this.screenHeight = height;
    }

    public void setScreenWidth(float width) {
        this.screenWidth = width;
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

    public MutableLiveData<Path> getSolutionLiveData() {
        return solutionLiveData;
    }

    public Path generateClosedPath(List<PointF> polyPoints) {
        Path wallPath = new Path();
        if (!polyPoints.isEmpty()) {
            PointF firstPoint = polyPoints.get(0);
            wallPath.moveTo(polyPoints.get(0).x, polyPoints.get(0).y);
            for (int j = 1; j < polyPoints.size(); j++) {
                PointF p = polyPoints.get(j);
                wallPath.lineTo(p.x, p.y);
            }
            wallPath.close();
        }
        return wallPath;
    }

    class MazeSolverRunnable implements Runnable {

        float scale;
        float xOffset;
        float yOffset;
        PathFinder pf;

        public MazeSolverRunnable() {
            pf = new PathFinder(Math.round(mazeHeight),
                    Math.round(mazeWidth),
                    startPoint,
                    endPoint);
            scale = Math.min(screenWidth / mazeWidth, screenHeight / mazeHeight);
            xOffset = (float) ((screenWidth - mazeWidth * scale) / 2.0);
            yOffset = (float) ((screenHeight - mazeHeight * scale) / 2.0);
        }


        @Override
        public void run() {
            try {
                getPathsFromSurfaces(rigidsurfaces);
                pf.setUpGraph();
                pathFinder.postValue(pf);
                solutionLiveData.postValue(generateClosedPath(pf.findPaths()));
            } catch (Throwable t) {
                Log.d("MazeRunnable", t.getMessage());
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



