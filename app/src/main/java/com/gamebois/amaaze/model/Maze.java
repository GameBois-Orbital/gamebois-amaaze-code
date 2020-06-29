package com.gamebois.amaaze.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.UUID;

public class Maze {
    private String uniqueID = UUID.randomUUID().toString();
    private String title;
    private String imageURL;
    @ServerTimestamp
    private Date timeCreated;
    private boolean isPublic = false;
    private int numLikes;

    public Maze() {
    }

    public Maze(String title, String imageURL, boolean isPublic) {
        this.title = title;
        this.imageURL = imageURL;
        this.isPublic = isPublic;
        numLikes = 0;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
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

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public Date getTime() {
        return timeCreated;
    }

    //Testing
//    public static Maze genNewMaze(int i) {
//        return new Maze("Default " + i, "https://via.placeholder.com/300.png");
//    }
}
