package com.gamebois.amaaze.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.Maze;
import com.gamebois.amaaze.viewmodel.MazifyActivityViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MazifyActivity extends AppCompatActivity {

    private static final String TAG = "MazifyActivity";
    private Button saveButton;
    private MazifyActivityViewModel mViewModel;
    private TextWatcher createMazeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String titleInput = s.toString().trim();
            mViewModel.setTitle(titleInput);
            saveButton.setEnabled(!titleInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mazify);
        //Set the toolbar and enable up navigation
        Toolbar mToolbar = findViewById(R.id.mazify_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //ViewModel
        mViewModel = new ViewModelProvider(this).get(MazifyActivityViewModel.class);
        //Initialise UI elements
        saveButton = findViewById(R.id.save_button);
        initTitleEdit();
        initPublicToggle();
        initWormholePicker();
    }

    private void initTitleEdit() {
        EditText titleEdit = findViewById(R.id.maze_title_edit);
        titleEdit.addTextChangedListener(createMazeTextWatcher);
    }

    private void initPublicToggle() {
        Switch publicToggle = (Switch) findViewById(R.id.public_switch);
        publicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mViewModel.setPublic(true);
                } else {
                    mViewModel.setPublic(false);
                }
            }
        });
    }

    private void initWormholePicker() {
        NumberPicker wormholePicker = findViewById(R.id.wormhole_picker);
        wormholePicker.setMinValue(1);
        wormholePicker.setMaxValue(3);
        wormholePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mViewModel.setWormholeDifficulty(newVal);
            }
        });
    }


    public void saveMaze(View view) {
        String title = mViewModel.getTitle();
        long wormholeDifficulty = mViewModel.getWormholeDifficulty();
        boolean isPublic = mViewModel.isPublic();
        Maze newMaze = new Maze(title, "https://picsum.photos/id/299/300", isPublic);
        newMaze.setWormholeDifficulty(wormholeDifficulty);
        FirebaseFirestore.getInstance().collection("mazes")
                .add(newMaze)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MazifyActivity.this, "Added maze", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MazifyActivity.this, "Error adding maze", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }
                });
    }
}