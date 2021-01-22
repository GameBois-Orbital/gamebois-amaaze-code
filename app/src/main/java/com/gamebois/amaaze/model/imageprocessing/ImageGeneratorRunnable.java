package com.gamebois.amaaze.model.imageprocessing;

import android.graphics.Bitmap;
import android.util.Log;

import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.repository.MazeRepository;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ImageGeneratorRunnable implements Runnable {

    public static final String LOG_TAG = ImageGeneratorRunnable.class.getSimpleName();

    Mat frame;
    Bitmap mExtraContourBitmap;
    Maze maze;

    public ImageGeneratorRunnable(Mat frame, Bitmap mExtraContourBitmap, Maze maze) {
        this.frame = frame;
        this.mExtraContourBitmap = mExtraContourBitmap;
        this.maze = maze;
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
