package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.livedata.MazeLiveData;
import com.gamebois.amaaze.model.Maze;

public abstract class MazeViewModel extends ViewModel {

    public abstract MazeLiveData getMazes();

    public abstract void add(Maze maze);
}
