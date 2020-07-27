package com.gamebois.amaaze.repository;

import android.util.Log;

import androidx.annotation.Nullable;

import com.gamebois.amaaze.model.Score;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class ScoreRepository {

    private static final String TAG = "Score Repository";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private OnFirestoreScoreTaskComplete firestoreScoreTaskComplete;
    private ListenerRegistration mRegistration;
    private Query mQuery;

    public ScoreRepository(OnFirestoreScoreTaskComplete firestoreTaskComplete) {
        this.firestoreScoreTaskComplete = firestoreTaskComplete;
    }

    private static Score addScore(String timing, FirebaseUser user) {
        Score score = new Score();
        score.setUserID(user.getUid());
        score.setUsername(user.getDisplayName());
        if (user.getPhotoUrl() != null) {
            score.setProfileURL(user.getPhotoUrl().toString());
        }
        score.setTiming(timing);
        return score;
    }

    public static void addScoreIfHighscore(final String timing, String mazeID) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference scoreRef = FirebaseFirestore.getInstance()
                .collection("scores")
                .document(mazeID)
                .collection("values")
                .document(user.getUid());
        scoreRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Score oldScore = documentSnapshot.toObject(Score.class);
                            if (oldScore.getTiming().compareTo(timing) > 0) {
                                oldScore.setTiming(timing);
                                scoreRef.set(oldScore, SetOptions.mergeFields("timing"));
                            }
                        } else {
                            scoreRef.set(addScore(timing, user));
                        }
                    }
                });
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