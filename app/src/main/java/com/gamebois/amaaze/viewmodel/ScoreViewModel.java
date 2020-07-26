package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.ViewModel;

import com.gamebois.amaaze.repository.ScoreRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ScoreViewModel extends ViewModel {

    private String documentID;
    private String timing;
    private FirebaseFirestore mFirestore;

    public ScoreViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public Query getQuery() {
        return mFirestore.collection("scores")
                .document(documentID)
                .collection("values")
                .orderBy("timing", Query.Direction.DESCENDING);
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public void sendScore() {
        if (timing != null && documentID != null) {
            ScoreRepository.addScoreIfHighscore(timing, documentID);
        }
    }
}
