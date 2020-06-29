package com.gamebois.amaaze.livedata;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MazeLiveData
        extends MutableLiveData<List<DocumentSnapshot>>
        implements EventListener<QuerySnapshot> {

    private static String TAG = "MazeLiveData";
    private List<DocumentSnapshot> mSnapshots;
    private Query mQuery;
    private ListenerRegistration mRegistration;

    public MazeLiveData(Query mQuery) {
        mSnapshots = new ArrayList<>();
        this.mQuery = mQuery;
        updateLiveData();
    }

    @Override
    protected void onActive() {
        mRegistration = mQuery.addSnapshotListener(this);
        super.onActive();
    }

    @Override
    protected void onInactive() {
        mRegistration.remove();
        super.onInactive();
    }


    @Override
    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
        //TODO: Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
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

                updateLiveData();
            }
        }
    }

    private void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
        }
    }

    private void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
    }

    private void updateLiveData() {
        setValue(mSnapshots);
    }


}
