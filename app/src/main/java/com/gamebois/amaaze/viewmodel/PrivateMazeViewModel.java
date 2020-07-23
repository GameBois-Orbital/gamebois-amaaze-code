package com.gamebois.amaaze.viewmodel;

import com.gamebois.amaaze.repository.MazeRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PrivateMazeViewModel extends MazeViewModel {

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private MazeRepository mazeRepository;

    public PrivateMazeViewModel() {
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }
//getInstance
//    public void setInterface (MazeRepository.OnFirestoreTaskComplete onFirestoreTaskComplete) {
//        mazeRepository = new MazeRepository(onFirestoreTaskComplete);
//    }

    @Override
    public Query getQuery() {
        return mFirestore.collection("mazes")
                .whereEqualTo("userID", mUser.getUid())
                .orderBy("numLikes", Query.Direction.DESCENDING);
    }
}
