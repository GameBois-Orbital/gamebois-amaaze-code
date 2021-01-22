package com.gamebois.amaaze.model.pathfinding;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.gamebois.amaaze.model.ContourList;

import java.util.ArrayList;
import java.util.List;

public class MazeSolverPathFinderRunnable implements Runnable, MazeObserver {

    private final List<ContourList> rigidSurfaces;
    private final MutableLiveData<List<Path>> pathLiveData;
    PathFinder pf;
    MutableLiveData<Node[]> gridLiveData;


    public MazeSolverPathFinderRunnable(float creatorHeight, float creatorWidth, List<ContourList> rigidSurfaces,
                                        MutableLiveData<List<Path>> pathLiveData, PointF startPoint, PointF endPoint,
                                        MutableLiveData<Node[]> gridLiveData) {
        this.rigidSurfaces = rigidSurfaces;
        this.pathLiveData = pathLiveData;
        pf = new PathFinder(Math.round(creatorHeight),
                Math.round(creatorWidth),
                pathLiveData.getValue(),
                startPoint,
                endPoint);
        pf.subscribeToNodes(this);
        this.gridLiveData = gridLiveData;
    }


    @Override
    public void run() {
        try {
            getPathsFromSurfaces(rigidSurfaces);
//            pf.setUpGraph();
//            updateNodes();
//            boolean isSolvable = pf.findPaths();
        } catch (Throwable t) {
            Log.d("MazeRunnable", "Error generating paths");
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

    @Override
    public void updateNodes() {
        gridLiveData.postValue(pf.nodes);
    }
}
