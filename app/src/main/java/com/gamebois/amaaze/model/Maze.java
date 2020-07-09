package com.gamebois.amaaze.model;

import android.graphics.PointF;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Maze {
    @DocumentId
    private String uniqueID;
    @ServerTimestamp
    private Date timeCreated;
    private String title;
    private String imageURL;
    private boolean isPublic;
    private long wormholeDifficulty;
    private long numLikes;
    private int colour;
    private List<List<PointF>> contours;

    public Maze() {
    }

    public Maze(String title, String imageURL, boolean isPublic) {
        this.title = title;
        this.imageURL = imageURL;
        this.isPublic = isPublic;
        numLikes = 0;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public long getWormholeDifficulty() {
        return wormholeDifficulty;
    }

    public void setWormholeDifficulty(long wormholeDifficulty) {
        this.wormholeDifficulty = wormholeDifficulty;
    }
}
