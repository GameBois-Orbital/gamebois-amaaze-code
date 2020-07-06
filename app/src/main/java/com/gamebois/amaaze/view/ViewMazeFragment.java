package com.gamebois.amaaze.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.view.adapters.ViewMazesAdapter;
import com.gamebois.amaaze.viewmodel.MazeViewModel;
import com.gamebois.amaaze.viewmodel.PrivateMazeViewModel;
import com.gamebois.amaaze.viewmodel.PublicMazeViewModel;

public class ViewMazeFragment extends Fragment {

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
        Context activityContext = view.getContext();
        //Pass in the values from ViewModel into the adapter
        view.setLayoutManager(new LinearLayoutManager(activityContext));
        mAdapter = new ViewMazesAdapter(activityContext, mViewModel.getQuery());
        view.setAdapter(mAdapter);
        view.setHasFixedSize(true);
        view.setItemViewCacheSize(15);

    }


}