package com.gamebois.amaaze.model;

import android.graphics.PointF;

import java.util.ArrayList;

public class ContourList {

    private ArrayList<PointF> contourList;

    public ContourList() {
    }

    public ContourList(ArrayList<PointF> contourList) {
        this.contourList = contourList;
    }

    public ArrayList<PointF> getContourList() {
        return contourList;
    }

    public void setContourList(ArrayList<PointF> contourList) {
        this.contourList = contourList;
    }

}
