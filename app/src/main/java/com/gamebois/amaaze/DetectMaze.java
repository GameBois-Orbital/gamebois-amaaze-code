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
    private Mat processed = new Mat();
    private Mat filtered = new Mat();
    private Size unhole = new Size(15,15);
    private Scalar color = new Scalar(0, 255, 0);
    public List<MatOfPoint> contours;
    public ArrayList<PointF> android ;

    float scale;
    float xoffset;
    float yoffset;

    public DetectMaze(int screen_width,int screen_height, int mat_width, int mat_height) {
        List<MatOfPoint> contours = new ArrayList<>();
        android = new ArrayList<>();

        scale= Math.min( (float)screen_width/mat_width, (float)screen_height/mat_height );
        xoffset= (float) ((screen_width-scale*mat_width)/2.0); //shift the points X coordiante by xoffset (defined in opencv CameraBridgeViewBase.java at line 420)
        yoffset=(float) ((screen_height-scale*mat_height)/2.0);
    }

    public void makeMazePoints(List<MatOfPoint> contours, int max_index) {
        List<Point> cvContour = contours.get(max_index).toList();

        for (int j = 0; j < cvContour.size(); j++) {
            Point p = cvContour.get(j);
            android.add(new PointF((float) p.x*scale + xoffset, (float) (p.y*scale+yoffset)));
        }
    }

    public ArrayList<PointF> getMazePoints(){
        return android;
    }
    

    public void process(Mat frame) {
        contours.clear();
        processed.release();
        filtered.release();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(frame, processed, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.bilateralFilter(processed, filtered, 3, 170, 3);
        Imgproc.adaptiveThreshold(filtered, processed, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 7); // apply adaptive thresolding to blured frame
        Imgproc.dilate(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, unhole, new Point(0, 0)));
        Imgproc.erode(processed, processed, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, unhole, new Point(0, 0)));
        Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxVal = 0;
        int max_index = -1;

        for (int i = 0; i < contours.size(); i++) {
            double contourArea = Imgproc.contourArea(contours.get(i));
            if (maxVal < contourArea) {
                maxVal = contourArea;
                max_index = i;
            }
        }
        if( max_index >=0) {
            Imgproc.drawContours(frame, contours, max_index, color, 2, Core.LINE_8, hierarchy, 0, new Point());
            makeMazePoints(contours, max_index);
        }
    }
}
