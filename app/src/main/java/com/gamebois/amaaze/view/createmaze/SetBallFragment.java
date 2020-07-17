package com.gamebois.amaaze.view.createmaze;

import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.viewmodel.MazifyActivityViewModel;

import java.util.List;

public class SetBallFragment extends Fragment implements View.OnClickListener {

    private String LOG_TAG = SetBallFragment.class.getSimpleName();
    private MazifyActivityViewModel mViewModel;
    private DrawMazeView drawMazeView;
    private Button nextButton;

    public SetBallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_ball, container, false);
        drawMazeView = (DrawMazeView) view.findViewById(R.id.draw_maze_view);
        nextButton = (Button) view.findViewById(R.id.button_to_details);
        nextButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MazifyActivityViewModel.class);
        mViewModel.getPaths().observe(getViewLifecycleOwner(), new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> pathList) {
                if (pathList != null) {
                    drawMazeView.setPaths(pathList);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_to_details:
                nextButton.setEnabled(false);
                Navigation.findNavController(v).navigate(R.id.action_setBallFragment_to_addInfoFragment);
        }
    }
}