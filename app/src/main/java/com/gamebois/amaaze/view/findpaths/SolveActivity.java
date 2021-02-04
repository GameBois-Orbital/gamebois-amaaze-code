package com.gamebois.amaaze.view.findpaths;

import android.graphics.PointF;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.view.viewmaze.ViewMazeFragment;
import com.gamebois.amaaze.viewmodel.SolveActivityViewModel;

import java.util.List;

public class SolveActivity extends AppCompatActivity {

    SolveActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve);
        Bundle b = getIntent().getExtras();
        mViewModel = new ViewModelProvider(this).get(SolveActivityViewModel.class);
        mViewModel.setID(b.getString(ViewMazeFragment.MAZE_ID_TAG));
        List<Float> startPoint = (List<Float>) b.getSerializable(ViewMazeFragment.MAZE_START_POINT);
        List<Float> endPoint = (List<Float>) b.getSerializable(ViewMazeFragment.MAZE_END_POINT);
        mViewModel.setStartPoint(new PointF(startPoint.get(0), startPoint.get(1)));
        mViewModel.setEndPoint(new PointF(endPoint.get(0), endPoint.get(1)));
        mViewModel.setRadius(b.getFloat(ViewMazeFragment.MAZE_RADIUS));
        mViewModel.setMazeHeight(b.getFloat(ViewMazeFragment.CREATOR_HEIGHT));
        mViewModel.setMazeWidth(b.getFloat(ViewMazeFragment.CREATOR_WIDTH));
    }
}