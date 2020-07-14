package com.gamebois.amaaze.model;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

public class PathList {

    private List<Path> pathList = new ArrayList<>();

    public PathList() {

    }

    public PathList(List<Path> pathList) {
        this.pathList = pathList;
    }

    public List<Path> getPathList() {
        return pathList;
    }

    public void setPathList(List<Path> pathList) {
        this.pathList = pathList;
    }
}
