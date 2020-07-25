package com.gamebois.amaaze.repository;

import android.util.Log;

import androidx.annotation.Nullable;

import com.gamebois.amaaze.model.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ScoreRepository {

    private static final String TAG = "Score Repository";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private OnFirestoreScoreTaskComplete firestoreScoreTaskComplete;
    private ListenerRegistration mRegistration;
    private Query mQuery;

    public ScoreRepository(OnFirestoreScoreTaskComplete firestoreTaskComplete) {
        this.firestoreScoreTaskComplete = firestoreTaskComplete;
    }

    public static void addScore(String timing, String mazeID) {
        Score score = new Score();
        score.setUserID(FirebaseAuth.getInstance().getUid());
        score.setTiming(timing);
        FirebaseFirestore.getInstance()
                .collection("scores")
                .document(mazeID)
                .collection("values")
                .add(score);
    }

    public void setQuery(Query mQuery) {
        stopListening();
        this.mQuery = mQuery;
        startListening();
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, e.toString());
                        return;
                    }
                    if (documentSnapshots != null) {
                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            DocumentSnapshot snapshot = change.getDocument();

                            switch (change.getType()) {
                                case ADDED:
                                    onDocumentAdded(change);
                                    break;
                                case MODIFIED:
                                    onDocumentModified(change);
                                    break;
                                case REMOVED:
                                    onDocumentRemoved(change);
                                    break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void onDocumentRemoved(DocumentChange change) {
        firestoreScoreTaskComplete.documentRemoved(change.getOldIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        firestoreScoreTaskComplete.documentUpdated(change.getOldIndex(), change.getNewIndex(), change.getDocument().toObject(Score.class));
    }

    private void onDocumentAdded(DocumentChange change) {
        firestoreScoreTaskComplete.documentAdded(change.getNewIndex(), change.getDocument().toObject(Score.class));
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        firestoreScoreTaskComplete.cleanupData();
    }


    public interface OnFirestoreScoreTaskComplete {
        void documentAdded(int index, Score s);

        void documentUpdated(int oldIndex, int newIndex, Score s);

        void documentRemoved(int index);

        void cleanupData();
    }
}