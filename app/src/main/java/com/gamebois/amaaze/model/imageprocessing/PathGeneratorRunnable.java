package com.gamebois.amaaze.model.imageprocessing;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.gamebois.amaaze.model.ContourList;

import java.util.ArrayList;
import java.util.List;

public class PathGeneratorRunnable implements Runnable {

    public static final String LOG_TAG = PathGeneratorRunnable.class.getSimpleName();

    MutableLiveData<List<Path>> pathLiveData;
    List<ContourList> rigidSurfaces;

    public PathGeneratorRunnable(List<ContourList> rigidSurfaces, MutableLiveData<List<Path>> pathLiveData) {
        this.rigidSurfaces = rigidSurfaces;
        this.pathLiveData = pathLiveData;
    }

    @Override
    public void run() {
        try {
            getPathsFromSurfaces(rigidSurfaces);
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
                //Take in pathfinder instance
                //arraypos = pathfinder.getPosAt(p.x, p.y)
                //pathfinder.setObstacle(p.x, p.y)
                wallPath.lineTo(p.x, p.y);
            }
            wallPath.close();
            paths.add(wallPath);
        }
        pathLiveData.postValue(paths);
    }
}
