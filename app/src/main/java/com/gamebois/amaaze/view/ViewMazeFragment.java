package com.gamebois.amaaze.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.view.adapters.ViewMazesAdapter;
import com.gamebois.amaaze.viewmodel.MazeViewModel;
import com.gamebois.amaaze.viewmodel.PrivateMazeViewModel;
import com.gamebois.amaaze.viewmodel.PublicMazeViewModel;

public class ViewMazeFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_PUBLIC = "public-bool";
    //Default number of columns is 1
    private int mColumnCount = 1;
    private boolean mIsPublic = false;
    private ViewMazesAdapter mAdapter;
    private MazeViewModel mViewModel;

    public ViewMazeFragment() {
        // Required empty public constructor
    }

    public static ViewMazeFragment newInstance(boolean isPublic) {
        ViewMazeFragment fragment = new ViewMazeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        args.putBoolean(ARG_PUBLIC, isPublic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public void initRecyclerView(RecyclerView view) {
//        view.setHasFixedSize(true);
        view.setItemViewCacheSize(15);
        Context activityContext = view.getContext();
        //Pass in the values from ViewModel into the adapter
        mAdapter = new ViewMazesAdapter(activityContext);
        view.setAdapter(mAdapter);
        //Set linear or grid layout based on number of columns defined
        if (mColumnCount <= 1) {
            view.setLayoutManager(new LinearLayoutManager(activityContext));
        } else {
            view.setLayoutManager(new GridLayoutManager(activityContext, mColumnCount));
        }
    }
}