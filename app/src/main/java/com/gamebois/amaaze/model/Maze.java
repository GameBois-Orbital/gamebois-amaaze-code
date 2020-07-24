package com.gamebois.amaaze.model;

import android.graphics.PointF;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Maze {
    @DocumentId
    private String uniqueID;
    @ServerTimestamp
    private Date timeCreated;
    private String title;
    private String imageURL;
    private String userID;
    private boolean isPublic;
    private long numLikes = 0;
    private float creatorHeight;
    private float creatorWidth;
    private List<Float> startPoint = new ArrayList<>(2);
    private List<Float> endPoint = new ArrayList<>(2);
    private float creatorRadius;
    private ArrayList<PointF> wormholeCentres;

    public Maze() {
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID() {
        this.uniqueID = UUID.randomUUID().toString();
    }

    public Date getTimeCreated() {
        return timeCreated;
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

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public float getCreatorHeight() {
        return creatorHeight;
    }

    public void setCreatorHeight(float height) {
        this.creatorHeight = height;
    }

    public float getCreatorWidth() {
        return creatorWidth;
    }

    public void setCreatorWidth(float creatorWidth) {
        this.creatorWidth = creatorWidth;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public float getCreatorRadius() {
        return creatorRadius;
    }

    public void setCreatorRadius(float creatorRadius) {
        this.creatorRadius = creatorRadius;
    }

    public List<Float> getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(List<Float> startPoint) {
        this.startPoint = startPoint;
    }

    public List<Float> getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(List<Float> endPoint) {
        this.endPoint = endPoint;
    }

    public ArrayList<PointF> getWormholeCentres() {
        return wormholeCentres;
    }

    public void setWormholeCentres(ArrayList<PointF> wormholeCentres) {
        this.wormholeCentres = wormholeCentres;
    }
}
