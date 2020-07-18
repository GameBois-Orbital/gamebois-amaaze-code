package com.gamebois.amaaze.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.view.adapters.ViewMazesAdapter;
import com.gamebois.amaaze.viewmodel.MazeViewModel;
import com.gamebois.amaaze.viewmodel.PrivateMazeViewModel;
import com.gamebois.amaaze.viewmodel.PublicMazeViewModel;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ViewMazeFragment extends Fragment implements ViewMazesAdapter.OnPlayListener {

    public static final String MAZE_ID_TAG = "Maze ID Tag";
    private static final String ARG_PUBLIC = "public-bool";
    private boolean mIsPublic = false;
    private ViewMazesAdapter mAdapter;
    private MazeViewModel mViewModel;

    public ViewMazeFragment() {
        // Required empty public constructor
    }

    public static ViewMazeFragment newInstance(boolean isPublic) {
        ViewMazeFragment fragment = new ViewMazeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PUBLIC, isPublic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mIsPublic = getArguments().getBoolean(ARG_PUBLIC);
        }

        if (mIsPublic) {
            mViewModel = new ViewModelProvider(this).get(PublicMazeViewModel.class);
        } else {
            mViewModel = new ViewModelProvider(this).get(PrivateMazeViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set it as a variable
        View listView = inflater.inflate(R.layout.fragment_view_maze, container, false);
        if (listView instanceof RecyclerView) {
            RecyclerView rView = (RecyclerView) listView;
            initRecyclerView(rView);
        }
        return listView;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                mAdapter.deleteMaze(viewHolder.getAdapterPosition());
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(ViewMazeFragment.this.getActivity(), R.color.warning))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null && mViewModel != null) {
            mAdapter.startListening(mViewModel.getQuery());
        }
    }

    public void initRecyclerView(RecyclerView view) {
        Context activityContext = view.getContext();
        //Pass in the values from ViewModel into the adapter
        view.setLayoutManager(new LinearLayoutManager(activityContext));
        mAdapter = new ViewMazesAdapter(activityContext, this);
        view.setAdapter(mAdapter);
        view.setHasFixedSize(true);
        view.setItemViewCacheSize(15);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(view);

    }


    @Override
    public void onPlayClick(String mazeID) {
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra(MAZE_ID_TAG, mazeID);
        startActivity(intent);
    }
}
