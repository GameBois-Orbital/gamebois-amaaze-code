package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gamebois.amaaze.model.Maze;

import java.util.ArrayList;
import java.util.List;

public class PublicMazeViewModel extends MazeViewModel {

    private MutableLiveData<List<Maze>> mazeList;

    public PublicMazeViewModel() {
        List<Maze> mazeTemp = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mazeTemp.add(Maze.genNewMaze(i));
        }
        mazeList = new MutableLiveData<>();
        mazeList.setValue(mazeTemp);
    }

    public LiveData<List<Maze>> getMazes() {
        return mazeList;
    }

}
