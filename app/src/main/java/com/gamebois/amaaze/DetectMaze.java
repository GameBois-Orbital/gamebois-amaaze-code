package com.gamebois.amaaze;

import android.graphics.PointF;

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

    private Mat processed;
    private Mat filtered;
    private Mat hierarchy;
    private Size unhole;
    private Scalar color;
    private List<MatOfPoint> contours;
    private double maxContourArea;
    private int maxContourIdx;
    float scale;
    float xoffset;
    float yoffset;

    public DetectMaze(int screen_width,int screen_height, int mat_width, int mat_height) {
        processed = new Mat();
        filtered = new Mat();
        hierarchy = new Mat();
        unhole = new Size(15,15);
        color = new Scalar(0, 255, 0);
        List<MatOfPoint> contours = new ArrayList<>();
        maxContourArea = 0;
        maxContourIdx = 0;
        scale= Math.min( (float)screen_width/mat_width, (float)screen_height/mat_height );
        xoffset= (float) ((screen_width-scale*mat_width)/2.0); //shift the points X coordiante by xoffset (defined in opencv CameraBridgeViewBase.java at line 420)
        yoffset=(float) ((screen_height-scale*mat_height)/2.0);
    }

    public List<PointF> getMazePoints() {
        List<Point> cv = contours.get(maxContourIdx).toList();
        List<PointF> android = new ArrayList<>();

        for (int j = 0; j < cv.size(); j++) {
            Point p = cv.get(j);
            android.add(new PointF((float) p.x*scale + xoffset, (float) (p.y*scale+yoffset)));
        }

        return  android;

    }
    

    public void process(Mat frame) {
        Imgproc.cvtColor(frame, processed, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.bilateralFilter(processed, filtered, 3, 170, 3);
        Imgproc.adaptiveThreshold(filtered, processed, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 7); // apply adaptive thresolding to blured frame
        Imgproc.dilate(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, unhole, new Point(-1, -1)));
        Imgproc.erode(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, unhole, new Point(-1, -1)));

        Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);


        for (int i = 0; i< contours.size(); i++) {
            double contourArea = Imgproc.contourArea(contours.get(i));
            if (maxContourArea < contourArea) {
                maxContourArea = contourArea;
                maxContourIdx = i;
            }
        }

        Imgproc.drawContours(frame, contours, maxContourIdx, color, 2, Core.LINE_8, hierarchy, 0, new Point());
    }
}
