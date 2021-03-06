package com.gamebois.amaaze.repository;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.model.Maze;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MazeRepository {

    private static final String TAG = "MazeRepository";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private OnFirestoreTaskComplete firestoreTaskComplete;
    private ListenerRegistration mRegistration;
    private Query mQuery;

    public MazeRepository(OnFirestoreTaskComplete firestoreTaskComplete) {
        this.firestoreTaskComplete = firestoreTaskComplete;
    }

    public static Task<Void> addMaze(Maze m) {
        m.setUserID(FirebaseAuth.getInstance()
                .getUid());
        return FirebaseFirestore.getInstance()
                .collection("mazes")
                .document(m.getUniqueID())
                .set(m, SetOptions.merge());
    }

    public static Task<Void> addMaze(Maze m, List<ContourList> rigidSurfaces) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        WriteBatch batch = firestore.batch();
        m.setUserID(FirebaseAuth.getInstance()
                .getUid());
        DocumentReference newMaze = firestore
                .collection("mazes")
                .document(m.getUniqueID());
        CollectionReference newMazeContours = newMaze
                .collection("contours");
        batch.set(newMaze, m);
        for (ContourList contourInstance : rigidSurfaces) {
            DocumentReference newDoc = newMazeContours.document();
            batch.set(newDoc, contourInstance, SetOptions.merge());
        }
        return batch.commit();
    }

    public static List<ContourList> getMazeContours(String documentID) {
        return FirebaseFirestore.getInstance()
                .collection("mazes")
                .document(documentID)
                .collection("contours")
                .get()
                .getResult()
                .toObjects(ContourList.class);
    }

    public static void updateMaze(Maze m) {

    }

    public static void generateImage(Bitmap mExtraContourBitmap, final Maze m) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://amaaze-6da22.appspot.com/");
        String referenceID = m.getUniqueID() + ".png";
        final StorageReference pictureRef = storageRef.child(referenceID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mExtraContourBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = pictureRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pictureRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                m.setImageURL(uri.toString());
                                FirebaseFirestore.getInstance()
                                        .collection("mazes")
                                        .document(m.getUniqueID())
                                        .set(m, SetOptions.merge());
                            }
                        });
            }
        });
    }

    public Task<Void> deleteMaze(Maze m) {
        return firestore
                .collection("mazes")
                .document(m.getUniqueID())
                .delete();
    }

    public static void likeMaze(Maze m) {

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
                        Log.w(TAG, e.getMessage());
                        return;
                    }
                    if (documentSnapshots != null) {
                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            DocumentSnapshot snapshot = change.getDocument();

                            switch (change.getType()) {
                                case ADDED:
                                    onDocumentAdded(change);
                                    Log.d(TAG, "New Msg: " + change.getDocument().toObject(Maze.class));
                                    break;
                                case MODIFIED:
                                    onDocumentModified(change);
                                    Log.d(TAG, "Modified Msg: " + change.getDocument().toObject(Maze.class));
                                    break;
                                case REMOVED:
                                    onDocumentRemoved(change);
                                    Log.d(TAG, "Deleted Msg: " + change.getDocument().toObject(Maze.class));
                                    break;
                            }
                        }
                    }
                }
            });
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        firestoreTaskComplete.cleanupData();
    }

    private void onDocumentRemoved(DocumentChange change) {
        firestoreTaskComplete.documentRemoved(change.getOldIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        firestoreTaskComplete.documentUpdated(change.getOldIndex(), change.getNewIndex(), change.getDocument().toObject(Maze.class));
    }

    private void onDocumentAdded(DocumentChange change) {
        firestoreTaskComplete.documentAdded(change.getNewIndex(), change.getDocument().toObject(Maze.class));
    }


    public interface OnFirestoreTaskComplete {

        void documentAdded(int index, Maze m);

        void documentUpdated(int oldIndex, int newIndex, Maze m);

        void documentRemoved(int index);

        void cleanupData();
    }
}
