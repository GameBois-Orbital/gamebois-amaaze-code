package com.gamebois.amaaze.model;

public class Maze {
    private String title;
    private String imageURL;

    public Maze(String title, String imageURL) {
        this.title = title;
        this.imageURL = imageURL;
    }

    //Testing
    public static Maze genNewMaze(int i) {
        return new Maze("Default " + i, "http://via.placeholder.com/300.png");
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
}
