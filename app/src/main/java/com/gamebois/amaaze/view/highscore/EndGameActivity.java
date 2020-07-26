package com.gamebois.amaaze.view.highscore;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.view.GameActivity;
import com.gamebois.amaaze.viewmodel.ScoreViewModel;

public class EndGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        Intent received = getIntent();
        String timing = received.getStringExtra(GameActivity.TIME_TEXT);
        String mazeID = received.getStringExtra(GameActivity.MAZE_ID);
        ScoreViewModel mViewModel = new ViewModelProvider(this).get(ScoreViewModel.class);
        mViewModel.setTiming(timing);
        mViewModel.setDocumentID(mazeID);
        Toolbar mToolbar = findViewById(R.id.score_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}