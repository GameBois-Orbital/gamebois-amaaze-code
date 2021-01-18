package com.gamebois.amaaze.view.createmaze;

import android.graphics.PointF;

import com.gamebois.amaaze.model.ContourList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class DetectMaze {
    private static String LOG = DetectMaze.class.getSimpleName();

    private int MIN_CONTOUR_AREA = 200;

    private Mat processed = new Mat();
    private Mat filtered = new Mat();
    private Size unhole = new Size(7, 7);
    private Scalar color = new Scalar(0, 255, 0);
    private List<MatOfPoint> contours;
    public double biggest;
    public ArrayList<ContourList> rigidSurfaces;
    int screen_width, screen_height;

    float scale;
    float xoffset;
    float yoffset;


    public DetectMaze(int screen_width, int screen_height, int mat_width, int mat_height, float scale) {
        contours = new ArrayList<>();
        rigidSurfaces = new ArrayList<>();
        this.scale = scale;

        this.screen_width = screen_width;
        this.screen_height = screen_height;

        xoffset = (float) ((screen_width - scale * mat_width) / 2.0); //shift the points X coordiante by xoffset (defined in opencv CameraBridgeViewBase.java at line 420)
        yoffset = (float) ((screen_height - scale * mat_height) / 2.0);
    }

    public ArrayList<ContourList> getRigidSurfaces() {
        return rigidSurfaces;
    }

    public void process(Mat frame) {
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(frame, processed, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.bilateralFilter(processed, filtered, 3, 170, 3);
        Imgproc.adaptiveThreshold(filtered, processed, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 7); // apply adaptive thresolding to blured frame
        Imgproc.dilate(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, unhole, new Point(0, 0)));
        Imgproc.erode(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, unhole, new Point(0, 0)));
        Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        rigidSurfaces.clear();

        for (int i = 0; i < contours.size(); i++) {    // for every set of contours detected, decide whether to draw
            double contourArea = Imgproc.contourArea(contours.get(i));
            if (MIN_CONTOUR_AREA < contourArea) {
                Imgproc.drawContours(frame, contours, i, color, 2, Core.LINE_8, hierarchy, 0, new Point()); // for every contour drawn, add contourshifted to rigidsurfaces
                List<Point> contour = contours.get(i).toList();
                ArrayList<PointF> contourShifted = new ArrayList<>();
                for (int j = 0; j < contour.size(); j++) {  // convert contour to contourshifted
                    Point p = contour.get(j);
                    contourShifted.add(new PointF((float) p.x * this.scale + this.xoffset, (float) p.y * this.scale + this.yoffset));
                }
                rigidSurfaces.add(new ContourList(contourShifted));
            }
        }
    }
}




