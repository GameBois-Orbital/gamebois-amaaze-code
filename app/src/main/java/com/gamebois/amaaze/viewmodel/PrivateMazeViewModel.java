package com.gamebois.amaaze.viewmodel;

import android.util.Log;

import com.gamebois.amaaze.livedata.MazeLiveData;
import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.repository.MazeRepository;

public class PrivateMazeViewModel extends MazeViewModel {

    private MazeRepository mRepository = MazeRepository.getInstance();

    public PrivateMazeViewModel() {
    }

    public void add(Maze maze) {
        mRepository.addMaze(maze);
    }

    public MazeLiveData getMazes() {
        Log.d("LIVEDATA", "CALLED from VM");
        return mRepository.getPrivateByDate();
    }
}
