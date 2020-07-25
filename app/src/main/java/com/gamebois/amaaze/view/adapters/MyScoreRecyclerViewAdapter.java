package com.gamebois.amaaze.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.Score;
import com.gamebois.amaaze.repository.ScoreRepository;
import com.gamebois.amaaze.view.GlideApp;
import com.google.firebase.firestore.Query;

import java.util.List;


public class MyScoreRecyclerViewAdapter extends RecyclerView.Adapter<MyScoreRecyclerViewAdapter.ViewHolder>
        implements ScoreRepository.OnFirestoreScoreTaskComplete {

    public static final String LOG_TAG = MyScoreRecyclerViewAdapter.class.getSimpleName();
    private final Context context;
    private ScoreRepository mRepository;
    private List<Score> mScores;

    public MyScoreRecyclerViewAdapter(Context context) {
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Score score = mScores.get(position);
        holder.setScoreAttributes(score);
    }

    @Override
    public int getItemCount() {
        return this.mScores.size();
    }

    public void startListening(Query q) {
        mRepository = new ScoreRepository(this);
        mRepository.setQuery(q);
    }

    public void stopListening() {
        mRepository.stopListening();
    }

    @Override
    public void documentAdded(int index, Score s) {
        mScores.add(index, s);
        notifyItemInserted(index);
    }

    @Override
    public void documentUpdated(int oldIndex, int newIndex, Score s) {
        if (oldIndex == newIndex) {
            // Item changed but remained in same position
            mScores.set(oldIndex, s);
            notifyItemChanged(oldIndex);
        } else {
            // Item changed and changed position
            mScores.remove(oldIndex);
            mScores.add(newIndex, s);
            notifyItemMoved(oldIndex, newIndex);
        }
    }

    @Override
    public void documentRemoved(int index) {
        mScores.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public void cleanupData() {
        mScores.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView profileImage;
        public final TextView scoreText;
        public final TextView usernameText;

        public ViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.profile_image);
            usernameText = view.findViewById(R.id.score_username);
            scoreText = view.findViewById(R.id.score_title);
        }

        public void setScoreAttributes(Score score) {
            String userID = score.getUserID();
            usernameText.setText(score.getUsername());
            scoreText.setText(score.getTiming());
            GlideApp.with(profileImage.getContext())
                    .load(score.getProfileURL())
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .error(new ColorDrawable(Color.RED))
                    .dontAnimate()
                    .into(profileImage);
        }
    }
}