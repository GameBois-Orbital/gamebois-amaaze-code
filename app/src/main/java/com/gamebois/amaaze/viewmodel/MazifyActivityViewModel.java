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
import com.gamebois.amaaze.view.createmaze.WormholePointsGenerator;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MazifyActivityViewModel extends ViewModel {

    public static final String LOG_TAG = MazifyActivityViewModel.class.getSimpleName();
    private boolean isPublic = true;
    private Maze maze;
    private String title;
    private MutableLiveData<List<Path>> pathLiveData;
    private ArrayList<PointF> wormholes;
    private List<ContourList> rigidSurfaces;
    private float creatorHeight;
    private float creatorWidth;
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

    public Task<Void> saveMaze() {
        maze = new Maze();
        maze.setUniqueID();
        if (title != null) {
            maze.setTitle(title);
        }
        maze.setStartPoint(Arrays.asList(
                startPoint.getmX(),
                startPoint.getmY()));
        maze.setEndPoint(Arrays.asList(endPoint.getmX(), endPoint.getmY()));
        maze.setCreatorRadius(startPoint.getRadius());
        maze.setCreatorHeight(creatorHeight);
        maze.setCreatorWidth(creatorWidth);
        maze.setWormholeCentres(generateWormholes());
        maze.setIsPublic(isPublic);
        if (rigidSurfaces != null) {
            return MazeRepository.addMaze(maze, rigidSurfaces);
        } else {
            return MazeRepository.addMaze(maze);
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


    public ArrayList<PointF> generateWormholes() {
        List<Path> paths = pathLiveData.getValue();
        Path start = new Path();
        Path end = new Path();
        start.addCircle(
                startPoint.getmX(),
                startPoint.getmY(),
                startPoint.getRadius(),
                Path.Direction.CW);
        end.addCircle(
                endPoint.getmX(),
                endPoint.getmY(),
                startPoint.getRadius(),
                Path.Direction.CW
        );
        paths.add(start);
        paths.add(end);
        return new WormholePointsGenerator(
                paths,
                creatorWidth,
                creatorHeight,
                startPoint.getRadius())
                .generate(8);
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

/*

List<PointF> polyPoints = surface.getContourList();
                Path wallPath = new Path();
                wallPath.moveTo(polyPoints.get(0).x * scale + xoffset, polyPoints.get(0).y * scale + yoffset);
                for (int j = 0; j < polyPoints.size(); j++) {
                    PointF p = polyPoints.get(j);
                    wallPath.lineTo(p.x * scale + xoffset, p.y * scale + yoffset);
                }
                wallPath.lineTo(polyPoints.get(0).x * scale + xoffset, polyPoints.get(0).y * scale + yoffset);
                wallPath.close();
                paths.add(wallPath);
            }


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
 */
