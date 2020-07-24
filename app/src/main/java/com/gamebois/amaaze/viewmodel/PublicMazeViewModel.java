package com.gamebois.amaaze.viewmodel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PublicMazeViewModel extends MazeViewModel {

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    public PublicMazeViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public Query getQuery() {
        return mFirestore.collection("mazes")
                .whereEqualTo("isPublic", true)
                .orderBy("numLikes", Query.Direction.DESCENDING);
    }

}
