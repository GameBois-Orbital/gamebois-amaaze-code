package com.gamebois.amaaze.viewmodel;

import com.gamebois.amaaze.repository.MazeRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PrivateMazeViewModel extends MazeViewModel {

    //TODO: Currently database logic is found in the viewmodel AND adapter classes. Need to refactor.

    private FirebaseFirestore mFirestore;
    private MazeRepository mazeRepository;

    public PrivateMazeViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
    }
//getInstance
//    public void setInterface (MazeRepository.OnFirestoreTaskComplete onFirestoreTaskComplete) {
//        mazeRepository = new MazeRepository(onFirestoreTaskComplete);
//    }

    @Override
    public Query getQuery() {
        return mFirestore.collection("mazes")
                .whereEqualTo("isPublic", false)
                .orderBy("numLikes", Query.Direction.DESCENDING);
    }
}
