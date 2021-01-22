package com.gamebois.amaaze.model.pathfinding;

import android.graphics.Path;
import android.graphics.PointF;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MazeSolverRunnable implements Runnable, MazeObserver {

    PathFinder pf;
    MutableLiveData<Node[]> gridLiveData;

    public MazeSolverRunnable(float creatorHeight, float creatorWidth, LiveData<List<Path>> pathLiveData,
                              PointF startPoint, PointF endPoint, MutableLiveData<Node[]> gridLiveData) {
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
        pf.setUpGraph();
        updateNodes();
        boolean isSolvable = pf.findPaths();
    }

    @Override
    public void updateNodes() {
        gridLiveData.postValue(pf.nodes);
    }
}
