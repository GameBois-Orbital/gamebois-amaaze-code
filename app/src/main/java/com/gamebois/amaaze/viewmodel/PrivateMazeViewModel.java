package com.gamebois.amaaze.viewmodel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PrivateMazeViewModel extends MazeViewModel {

    //TODO: Currently database logic is found in the viewmodel AND adapter classes. Need to refactor.

    private FirebaseFirestore mFirestore;

    public PrivateMazeViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public Query getQuery() {
        return mFirestore.collection("mazes")
                .whereEqualTo("isPublic", false)
                .orderBy("numLikes", Query.Direction.DESCENDING);
    }
}
