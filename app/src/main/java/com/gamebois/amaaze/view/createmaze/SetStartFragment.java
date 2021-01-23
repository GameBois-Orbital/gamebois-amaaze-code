package com.gamebois.amaaze.view.createmaze;

import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.viewmodel.MazifyActivityViewModel;
import com.google.android.material.slider.Slider;

import java.util.List;

public class SetStartFragment extends Fragment implements View.OnClickListener {

    private String LOG_TAG = SetStartFragment.class.getSimpleName();
    private MazifyActivityViewModel mViewModel;
    private DrawMazeView drawMazeView;
    private ImageButton nextButton;
    private ImageButton backButton;
    private boolean canNavigate = false;
    private TextView instructionsText;
    private Slider sizeSlider;
    private float height;
    private float width;

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
        nextButton = view.findViewById(R.id.button_to_details);
        drawMazeView = view.findViewById(R.id.draw_maze_view);
        backButton = view.findViewById(R.id.back_to_start);
        sizeSlider = view.findViewById(R.id.size_picker);
        instructionsText = view.findViewById(R.id.set_ball_instructions);
        initialiseSlider();
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        return view;
    }

    private void initialiseSlider() {
        sizeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    drawMazeView.setPointsRadius(value);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MazifyActivityViewModel.class);
        mViewModel.getPaths().observe(getViewLifecycleOwner(), new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> pathList) {
                Log.d(LOG_TAG, "Path change called");
                if (pathList != null) {
                    Log.d(LOG_TAG, pathList.toString());
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
        instructionsText.setTextColor(getResources().getColor(R.color.colorSurface));
    }

    private void navigateToNextScreen() {
        canNavigate = false;
//        drawMazeView.pause();
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
