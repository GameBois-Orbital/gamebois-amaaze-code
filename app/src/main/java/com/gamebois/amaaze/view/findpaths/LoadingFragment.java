package com.gamebois.amaaze.view.findpaths;

import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.pathfinding.Node;
import com.gamebois.amaaze.viewmodel.SolveActivityViewModel;

import java.util.List;

public class LoadingFragment extends Fragment {

    public static final String LOG_TAG = LoadingFragment.class.getSimpleName();
    private TextView info;
    private ProgressBar progressBar;
    private SolveMazeView solveMazeView;
    private SolveActivityViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        info = view.findViewById(R.id.infoText);
        progressBar = view.findViewById(R.id.progressBar);
        solveMazeView = view.findViewById(R.id.solve_maze_view);
        getWidthAndHeight(solveMazeView);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(SolveActivityViewModel.class);
        loadContoursFromDatabase();
        initalisePaths();
        setUpGraph();
    }

    private void getWidthAndHeight(final SolveMazeView solveMazeView) {
        final ViewTreeObserver obs = solveMazeView.getViewTreeObserver();

        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewModel.setScreenHeight(solveMazeView.getHeight());
                mViewModel.setScreenWidth(solveMazeView.getWidth());
                mViewModel.setRigidsurfaces();
                solveMazeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void loadContoursFromDatabase() {
        mViewModel.areSurfacesRetrieved.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean areSurfacesRetrieved) {
                if (areSurfacesRetrieved) {
                    info.setText(R.string.loading_generating_paths);
                }
            }
        });
    }

    private void initalisePaths() {
        mViewModel.getPaths().observe(getViewLifecycleOwner(), new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> pathList) {
                Log.d(LOG_TAG, "Path change called");
                if (pathList != null) {
                    info.setText(R.string.loading_initialise_pathfinding);
                    solveMazeView.setPaths(pathList);
                }
            }
        });
    }

    private void setUpGraph() {
        mViewModel.getGrid().observe(getViewLifecycleOwner(), new Observer<Node[]>() {
            @Override
            public void onChanged(Node[] nodes) {
                if (nodes != null) {
                    solveMazeView.setGrid(nodes);
                    progressBar.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        solveMazeView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        solveMazeView.resume();
    }
}