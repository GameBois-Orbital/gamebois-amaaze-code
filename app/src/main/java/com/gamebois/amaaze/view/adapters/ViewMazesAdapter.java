package com.gamebois.amaaze.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.Maze;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewMazesAdapter extends RecyclerView.Adapter<ViewMazesAdapter.MazeViewHolder>
        implements EventListener<QuerySnapshot> {

    private static final String TAG = "ViewMazesAdapter";
    //Inflate the card layouts
    private LayoutInflater mInflater;
    private final FirebaseFirestore mFirestore;
    private List<DocumentSnapshot> mSnapshots = new ArrayList<>();
    private Query mQuery;
    private ListenerRegistration mRegistration;

    public ViewMazesAdapter(Context context) {
        mFirestore = FirebaseFirestore.getInstance();
        mInflater = LayoutInflater.from(context);
        mQuery = mFirestore.collection("mazes")
                .whereEqualTo("isPublic", true)
                .orderBy("numLikes", Query.Direction.DESCENDING);
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

            }
        }
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    private void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    @NonNull
    @Override
    public MazeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mCardView = mInflater.inflate(R.layout.view_single_maze, parent, false);
        return new MazeViewHolder(mCardView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MazeViewHolder holder, int position) {
        DocumentSnapshot mazeSnapshot = mSnapshots.get(position);
        holder.setMazeAttributes(mazeSnapshot);
    }

    @Override
    public int getItemCount() {
        return this.mSnapshots.size();
    }

    public class MazeViewHolder extends RecyclerView.ViewHolder {

        final ViewMazesAdapter mAdapter;
        //TODO: Add other views
        private final TextView mazeTitle;
        private final ImageView mazeImage;

        public MazeViewHolder(@NonNull View itemView, ViewMazesAdapter adapter) {
            super(itemView);
            mazeTitle = itemView.findViewById(R.id.mazeTitle);
            mazeImage = itemView.findViewById(R.id.mazeImage);
            this.mAdapter = adapter;
        }

        public void setMazeAttributes(DocumentSnapshot mazeSnapshot) {
            Maze mCurrent = mazeSnapshot.toObject(Maze.class);
            Log.d("Maze attributes", mCurrent.toString());
            mazeTitle.setText(mCurrent.getTitle());
            Glide.with(mazeImage.getContext())
                    .load(mCurrent.getImageURL())
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .error(new ColorDrawable(Color.RED))
                    .into(mazeImage);
        }
    }


}
