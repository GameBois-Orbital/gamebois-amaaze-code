package com.gamebois.amaaze.view.createmaze;

import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.viewmodel.MazifyActivityViewModel;

import java.util.List;

public class SetStartFragment extends Fragment implements View.OnClickListener {

    private String LOG_TAG = SetStartFragment.class.getSimpleName();
    private MazifyActivityViewModel mViewModel;
    private DrawMazeView drawMazeView;
    private ImageButton nextButton;
    private ImageButton backButton;
    private boolean canNavigate = false;
    private TextView instructionsText;

    public SetStartFragment() {
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
        nextButton = (ImageButton) view.findViewById(R.id.button_to_details);
        backButton = (ImageButton) view.findViewById(R.id.back_to_start);
        instructionsText = (TextView) view.findViewById(R.id.set_ball_instructions);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
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
    public void onPause() {
        super.onPause();
        drawMazeView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        drawMazeView.resume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_to_details:
                if (canNavigate) {
                    navigateToNextScreen();
                    Navigation.findNavController(v).navigate(R.id.action_setBallFragment_to_addInfoFragment);
                } else {
                    setUpEndScreen();
                }
                break;
            case R.id.back_to_start:
                setUpStartScreen();
                break;
        }
    }

    private void setUpStartScreen() {
        canNavigate = false;
        drawMazeView.focusStartPoint();
        removeBackButton();
        instructionsText.setText(R.string.set_start_point);
    }

    private void setUpEndScreen() {
        canNavigate = true;
        drawMazeView.focusEndPoint();
        initialiseBackButton();
        instructionsText.setText(R.string.set_end_point);
    }

    private void navigateToNextScreen() {
        canNavigate = false;
        mViewModel.setStartPoint(drawMazeView.startPoint);
        mViewModel.setEndPoint(drawMazeView.endPoint);
    }

    private void initialiseBackButton() {
        backButton.setVisibility(View.VISIBLE);
    }

    private void removeBackButton() {
        backButton.setVisibility(View.GONE);
    }
}