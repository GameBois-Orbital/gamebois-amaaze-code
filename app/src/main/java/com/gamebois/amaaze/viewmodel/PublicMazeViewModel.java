package com.gamebois.amaaze.viewmodel;

import com.gamebois.amaaze.livedata.MazeLiveData;
import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.repository.MazeRepository;

public class PublicMazeViewModel extends MazeViewModel {

    private MazeRepository mRepository = MazeRepository.getInstance();

    public PublicMazeViewModel() {
    }

    public void add(Maze maze) {
        mRepository.addMaze(maze);
    }

    public MazeLiveData getMazes() {
        return mRepository.getPublicByDate();
    }

}
