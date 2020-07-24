package com.gamebois.amaaze.model;

import com.google.firebase.firestore.DocumentId;

public class Score {
    @DocumentId
    private String userID;
    private String timing;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }
}
