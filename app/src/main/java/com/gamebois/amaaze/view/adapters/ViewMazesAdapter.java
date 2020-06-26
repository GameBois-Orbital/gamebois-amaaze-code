package com.gamebois.amaaze.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.Maze;

import java.util.List;

public class ViewMazesAdapter extends RecyclerView.Adapter<ViewMazesAdapter.MazeViewHolder> {

    private static final String TAG = "ViewMazesAdapter";
    //Store the list values
    private final List<Maze> mazeList;
    //Inflate the card layouts
    private LayoutInflater mInflater;

    public ViewMazesAdapter(Context context, List<Maze> mazeList) {
        mInflater = LayoutInflater.from(context);
        this.mazeList = mazeList;
    }

    @NonNull
    @Override
    public MazeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mCardView = mInflater.inflate(R.layout.view_single_maze, parent, false);
        return new MazeViewHolder(mCardView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MazeViewHolder holder, int position) {
        Maze mCurrent = mazeList.get(position);
        holder.setMaze(mCurrent);
    }

    @Override
    public int getItemCount() {
        return this.mazeList.size();
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

        public void setMaze(Maze mCurrent) {
            mazeTitle.setText(mCurrent.getTitle());
            Glide.with(mazeImage.getContext())
                    .load(mCurrent.getImageURL())
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .error(new ColorDrawable(Color.RED))
                    .into(mazeImage);
        }
    }


}
