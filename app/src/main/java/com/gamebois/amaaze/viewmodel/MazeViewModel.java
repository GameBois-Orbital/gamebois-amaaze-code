package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.model.Maze;

import java.util.List;

public abstract class MazeViewModel extends ViewModel {

    public abstract LiveData<List<Maze>> getMazes();

}
