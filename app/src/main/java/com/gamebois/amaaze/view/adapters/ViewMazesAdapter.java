package com.gamebois.amaaze.view.adapters;

import android.content.Context;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.repository.MazeRepository;
import com.gamebois.amaaze.view.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ViewMazesAdapter extends RecyclerView.Adapter<ViewMazesAdapter.MazeViewHolder>
        implements MazeRepository.OnFirestoreTaskComplete {

    private MazeRepository mRepository;
    private static final String TAG = "ViewMazesAdapter";
    //Inflate the card layouts
    private final LayoutInflater mInflater;
    private List<Maze> mazeList = new ArrayList<>();
    private Context context;
    private OnPlayListener onPlayListener;

    public ViewMazesAdapter(Context context, OnPlayListener onPlayListener) {
        this.context = context;
        this.onPlayListener = onPlayListener;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MazeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mCardView = mInflater.inflate(R.layout.view_single_maze, parent, false);
        return new MazeViewHolder(mCardView, this, onPlayListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MazeViewHolder holder, int position) {
        Maze maze = mazeList.get(position);
        holder.setMazeAttributes(maze);
    }

    @Override
    public int getItemCount() {
        return this.mazeList.size();
    }

    public void startListening(Query q) {
        mRepository = new MazeRepository(this);
        mRepository.setQuery(q);
    }

    public void stopListening() {
        mRepository.stopListening();
    }

    @Override
    public void documentAdded(int index, Maze m) {
        mazeList.add(index, m);
        notifyItemInserted(index);
    }

    @Override
    public void documentUpdated(int oldIndex, int newIndex, Maze m) {
        if (oldIndex == newIndex) {
            // Item changed but remained in same position
            mazeList.set(oldIndex, m);
            notifyItemChanged(oldIndex);
        } else {
            // Item changed and changed position
            mazeList.remove(oldIndex);
            mazeList.add(newIndex, m);
            notifyItemMoved(oldIndex, newIndex);
        }
    }

    @Override
    public void documentRemoved(int index) {
        mazeList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public void cleanupData() {
        mazeList.clear();
        notifyDataSetChanged();

    }

    public void deleteMaze(final int position) {
        mRepository.deleteMaze(mazeList.get(position))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Deleted Maze", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface OnPlayListener {
        void onPlayClick(String mazeID,
                         List<Float> startPoint,
                         List<Float> endPoint,
                         float radius,
                         float creatorHeight,
                         float creatorWidth,
                         ArrayList<PointF> wormholes);

        void onSolveClick(String mazeID,
                          List<Float> startPoint,
                          List<Float> endPoint,
                          float radius,
                          float creatorHeight,
                          float creatorWidth);
    }

    public class MazeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ViewMazesAdapter mAdapter;
        //TODO: Add other views
        private final TextView mazeTitle;
        private final ImageView mazeImage;
        private final Button playButton;
        private final Button solveButton;
        private final View cardView;
        private OnPlayListener onPlayListener;

        public MazeViewHolder(@NonNull View itemView, ViewMazesAdapter adapter, OnPlayListener onPlayListener) {
            super(itemView);
            this.onPlayListener = onPlayListener;
            mazeTitle = itemView.findViewById(R.id.mazeTitle);
            mazeImage = itemView.findViewById(R.id.mazeImage);
            playButton = itemView.findViewById(R.id.playButton);
            solveButton = itemView.findViewById(R.id.solveButton);
            cardView = itemView.findViewById(R.id.card);
            playButton.setOnClickListener(this);
            solveButton.setOnClickListener(this);

            this.mAdapter = adapter;
        }

        public void setMazeAttributes(Maze maze) {
            mazeTitle.setText(maze.getTitle());
            GlideApp.with(mazeImage.getContext())
                    .load(maze.getImageURL())
                    .placeholder(R.drawable.loading_image_bg)
                    .error(R.drawable.error_bg)
                    .into(mazeImage);
        }

        @Override
        public void onClick(View v) {
            int clickID = v.getId();
            int pos = getAdapterPosition();
            Maze m = mazeList.get(pos);
            if (clickID == R.id.playButton) {
                onPlayListener.onPlayClick(m.getUniqueID(),
                        m.getStartPoint(),
                        m.getEndPoint(),
                        m.getCreatorRadius(),
                        m.getCreatorHeight(),
                        m.getCreatorWidth(),
                        m.getWormholeCentres());
            } else if (clickID == R.id.solveButton) {
                onPlayListener.onSolveClick(m.getUniqueID(),
                        m.getStartPoint(),
                        m.getEndPoint(),
                        m.getCreatorRadius(),
                        m.getCreatorHeight(),
                        m.getCreatorWidth());
            }
        }
    }
}
