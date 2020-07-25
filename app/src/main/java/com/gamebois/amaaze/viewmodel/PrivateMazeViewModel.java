package com.gamebois.amaaze.viewmodel;

import com.gamebois.amaaze.repository.MazeRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PrivateMazeViewModel extends MazeViewModel {

    private FirebaseFirestore mFirestore;
    private String userID;
    private MazeRepository mazeRepository;

    public PrivateMazeViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
    }
//getInstance
//    public void setInterface (MazeRepository.OnFirestoreTaskComplete onFirestoreTaskComplete) {
//        mazeRepository = new MazeRepository(onFirestoreTaskComplete);
//    }

    @Override
    public Query getQuery() {
        return mFirestore.collection("mazes")
                .whereEqualTo("userID", userID)
                .orderBy("numLikes", Query.Direction.DESCENDING);
    }
}
