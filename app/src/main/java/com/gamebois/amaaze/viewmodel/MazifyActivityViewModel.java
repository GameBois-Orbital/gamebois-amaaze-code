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
import java.util.Arrays;
import java.util.List;

public class MazifyActivityViewModel extends ViewModel {

    public static final String LOG_TAG = MazifyActivityViewModel.class.getSimpleName();
    private boolean isPublic;
    private Maze maze;
    private String title;
    private MutableLiveData<List<Path>> pathLiveData;
    private List<ContourList> rigidSurfaces;
    private float creatorHeight;
    private float creatorWidth;
    private PointMarker endPoint;
    private PointMarker startPoint;
    private float viewWidth;
    private float viewHeight;

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
        maze.setStartPoint(Arrays.asList(startPoint.getmX(), startPoint.getmY()));
        maze.setEndPoint(Arrays.asList(endPoint.getmX(), endPoint.getmY()));
        maze.setCreatorRadius(startPoint.getRadius());
        maze.setCreatorHeight(creatorHeight);
        maze.setCreatorWidth(creatorWidth);
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

    public void setStartPoint(PointMarker startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(PointMarker endPoint) {
        this.endPoint = endPoint;
    }

    public float getCreatorHeight() {
        return creatorHeight;
    }

    public void setCreatorHeight(float creatorHeight) {
        this.creatorHeight = creatorHeight;
    }

    public float getCreatorWidth() {
        return creatorWidth;
    }

    public void setCreatorWidth(float creatorWidth) {
        this.creatorWidth = creatorWidth;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(float viewWidth) {
        this.viewWidth = viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(float viewHeight) {
        this.viewHeight = viewHeight;
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
            float scale = Math.min(viewWidth / creatorWidth, viewHeight / creatorHeight);
            float xoffset = (float) ((viewWidth - creatorWidth * scale) / 2.0);
            float yoffset = (float) ((viewHeight - creatorHeight * scale) / 2.0);
            final ArrayList<Path> paths = new ArrayList<>();
            for (ContourList surface : rigidSurfaces) {
                List<PointF> polyPoints = surface.getContourList();
                Path wallPath = new Path();
                wallPath.moveTo(polyPoints.get(0).x * scale + xoffset, polyPoints.get(0).y * scale + yoffset);
                for (int j = 1; j < polyPoints.size(); j++) {
                    PointF p = polyPoints.get(j);
                    wallPath.lineTo(p.x * scale + xoffset, p.y * scale + yoffset);
                }
                wallPath.lineTo(polyPoints.get(0).x * scale + xoffset, polyPoints.get(0).y * scale + yoffset);
                wallPath.close();
                paths.add(wallPath);
            }
            pathLiveData.postValue(paths);
        }
    }
}

/*
final ArrayList<Path> paths = new ArrayList<>();
            for (ContourList surface : rigidSurfaces) {
                List<PointF> polyPoints = surface.getContourList();
                Path wallPath = new Path();
                wallPath.moveTo(polyPoints.get(0).x * scale + xoffset, polyPoints.get(0).y * scale + yoffset);
                for (int j = 1; j < polyPoints.size(); j++) {
                    PointF p = polyPoints.get(j);
                    wallPath.lineTo(p.x * scale + xoffset, p.y * scale + yoffset);
                }
                wallPath.lineTo(polyPoints.get(0).x * scale + xoffset, polyPoints.get(0).y * scale + yoffset);
                wallPath.close();
                paths.add(wallPath);
            }
            pathLiveData.postValue(paths);
        }
 */
