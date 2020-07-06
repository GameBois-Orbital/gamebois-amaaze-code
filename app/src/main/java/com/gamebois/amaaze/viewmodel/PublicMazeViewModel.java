package com.gamebois.amaaze.viewmodel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PublicMazeViewModel extends MazeViewModel {

    private FirebaseFirestore mFirestore;

    public PublicMazeViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public Query getQuery() {
        return mFirestore.collection("mazes")
                .whereEqualTo("isPublic", true)
                .orderBy("numLikes", Query.Direction.DESCENDING);
    }

}
