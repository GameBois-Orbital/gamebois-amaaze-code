package com.gamebois.amaaze.view.highscore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.viewmodel.ScoreViewModel;

public class ViewTimingFragment extends Fragment implements View.OnClickListener {

    private Button nextButton;
    private TextView timingText;
    private ScoreViewModel mViewModel;

    public ViewTimingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_timing, container, false);
        timingText = (TextView) view.findViewById(R.id.timing_content);
        timingText.setText(R.string.maze_loader_text);
        nextButton = (Button) view.findViewById(R.id.button_to_highscores);
        nextButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);
        timingText.setText(mViewModel.getTiming());
        mViewModel.sendScore();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_to_highscores) {
            Navigation.findNavController(v).navigate(R.id.action_viewTimingFragment_to_scoreFragment);
        }
    }
}