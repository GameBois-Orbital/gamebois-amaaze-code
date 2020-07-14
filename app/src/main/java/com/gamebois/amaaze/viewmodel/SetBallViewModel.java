package com.gamebois.amaaze.viewmodel;

import android.graphics.Path;

import androidx.lifecycle.ViewModel;

import java.util.List;

public class SetBallViewModel extends ViewModel {

    private List<Path> paths = null;
    private boolean pathsAreSet = false;

    public boolean checkIfPathsAreSet() {
        return this.paths != null && pathsAreSet;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
        this.pathsAreSet = true;
    }
}
