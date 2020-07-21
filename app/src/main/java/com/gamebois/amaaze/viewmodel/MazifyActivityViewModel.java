package com.gamebois.amaaze.viewmodel;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.graphics.PointMarker;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.repository.MazeRepository;

import java.util.ArrayList;
import java.util.List;

public class MazifyActivityViewModel extends ViewModel {

    public static final String LOG_TAG = MazifyActivityViewModel.class.getSimpleName();
    private boolean isPublic;
    private Maze maze;
    private String title;
    private MutableLiveData<List<Path>> pathLiveData;
    private List<ContourList> rigidSurfaces;
    private float height;
    private float width;
    private PointMarker endPoint;
    private PointMarker startPoint;

    public MazifyActivityViewModel() {
        pathLiveData = new MutableLiveData<>();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContourList> getRigidSurfaces() {
        return rigidSurfaces;
    }

    public void setRigidSurfaces(List<ContourList> rigidSurfaces) {
        this.rigidSurfaces = rigidSurfaces;
        pathLiveData.setValue(null);
    }

    public void saveMaze() {
        maze = new Maze();
        maze.setUniqueID();
        if (title != null) {
            maze.setTitle(title);
        }
        maze.setStartPoint(startPoint);
        maze.setEndPoint(endPoint);
        maze.setHeight(height);
        maze.setWidth(width);
        maze.setIsPublic(isPublic);
        if (rigidSurfaces != null) {
            MazeRepository.addMaze(maze, rigidSurfaces);
        } else {
            MazeRepository.addMaze(maze);
        }
    }

    public LiveData<List<Path>> getPaths() {
        if (pathLiveData.getValue() == null) {
            setPaths();
        }
        return pathLiveData;
    }

    private void setPaths() {
        PathGeneratorRunnable runnable = new PathGeneratorRunnable();
        new Thread(runnable).start();
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

    public void setStartPoint(PointMarker startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(PointMarker endPoint) {
        this.endPoint = endPoint;
    }

    class PathGeneratorRunnable implements Runnable {

        public PathGeneratorRunnable() {
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
                    wallPath.lineTo(p.x, p.y);
                }
                wallPath.close();
                paths.add(wallPath);
            }
            pathLiveData.postValue(paths);
        }
    }
}
