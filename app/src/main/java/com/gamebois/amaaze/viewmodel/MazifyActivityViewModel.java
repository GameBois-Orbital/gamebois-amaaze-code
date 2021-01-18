package com.gamebois.amaaze.viewmodel;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.graphics.PointMarker;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.model.WormholePointsGenerator;
import com.gamebois.amaaze.repository.MazeRepository;
import com.google.android.gms.tasks.Task;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MazifyActivityViewModel extends ViewModel {

    public static final String LOG_TAG = MazifyActivityViewModel.class.getSimpleName();

    private Maze maze;

    //Maze attributes defined by users
    private String title;
    private boolean isPublic = true;
    private List<ContourList> rigidSurfaces;
    private PointMarker endPoint;
    private PointMarker startPoint;

    //Maze attributes for graphics rendering
    private float creatorHeight;
    private float creatorWidth;
    private MutableLiveData<List<Path>> pathLiveData;
    private ArrayList<PointF> wormholes;

    private Bitmap mExtraContourBitmap;

    public MazifyActivityViewModel() {
        pathLiveData = new MutableLiveData<>();
        maze = new Maze();
        maze.setUniqueID();
    }

    //Setters for user-defined attributes

    public void setPublic(boolean mPublic) {
        this.isPublic = mPublic;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRigidSurfaces(List<ContourList> rigidSurfaces) {
        this.rigidSurfaces = rigidSurfaces;
        pathLiveData.setValue(null);
    }

    public void setStartPoint(PointMarker startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(PointMarker endPoint) {
        this.endPoint = endPoint;
    }

    //Setters for graphics rendering attributes

    public void setCreatorHeight(float creatorHeight) {
        this.creatorHeight = creatorHeight;
    }

    public void setCreatorWidth(float creatorWidth) {
        this.creatorWidth = creatorWidth;
    }

    /**
     * In a separate thread, obtain Paths from contour list and update pathLiveData
     */

    private void setPaths() {
        PathGeneratorRunnable runnable = new PathGeneratorRunnable();
        new Thread(runnable).start();
    }

    public LiveData<List<Path>> getPaths() {
        if (pathLiveData.getValue() == null) {
            setPaths();
        }
        return pathLiveData;
    }

    public void setMat(Mat frame) {
        Log.d(LOG_TAG, "FRAME 1: " + frame.height() + " " + frame.width());
        this.mExtraContourBitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888);
        generateImage(frame);
    }

    private void generateImage(Mat frame) {
        ImageGeneratorRunnable runnable = new ImageGeneratorRunnable(frame);
        new Thread(runnable).start();
    }

    public Task<Void> saveMaze() {
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

    class ImageGeneratorRunnable implements Runnable {

        Mat frame;

        public ImageGeneratorRunnable(Mat frame) {
            this.frame = frame;
        }

        @Override
        public void run() {
            try {
                Log.d(LOG_TAG, "FRAME: " + frame.height() + " " + frame.width());
                Log.d(LOG_TAG, "BITMAP: " + mExtraContourBitmap.getHeight() + " " + mExtraContourBitmap.getWidth());
                Utils.matToBitmap(frame, mExtraContourBitmap, true);
                MazeRepository.generateImage(mExtraContourBitmap, maze);
            } catch (Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        }
    }
}
