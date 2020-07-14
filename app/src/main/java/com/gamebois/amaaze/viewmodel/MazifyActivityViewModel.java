package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.ViewModel;

public class MazifyActivityViewModel extends ViewModel {

    private boolean isPublic;
    private String title;
    private long wormholeDifficulty;

    public MazifyActivityViewModel() {
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getWormholeDifficulty() {
        return wormholeDifficulty;
    }

    public void setWormholeDifficulty(long wormholeDifficulty) {
        this.wormholeDifficulty = wormholeDifficulty;
    }
}
