package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ScoreViewModel extends ViewModel {

    private String documentID;
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
}
