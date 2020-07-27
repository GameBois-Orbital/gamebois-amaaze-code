package com.gamebois.amaaze.view.createmaze;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.viewmodel.MazifyActivityViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AddInfoFragment.class.getSimpleName();
    private Button saveButton;
    private SwitchMaterial publicToggle;
    private View loadingScreen;
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

    public AddInfoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_info, container, false);
        //Initialise UI elements
        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        EditText titleEdit = view.findViewById(R.id.maze_title_edit);
        loadingScreen = view.findViewById(R.id.loading_screen);
        titleEdit.addTextChangedListener(createMazeTextWatcher);
        publicToggle = view.findViewById(R.id.public_switch);
        initPublicToggle();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MazifyActivityViewModel.class);
    }

    private void initPublicToggle() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                loadingScreen.setVisibility(View.VISIBLE);
                saveButton.setEnabled(false);
                mViewModel.saveMaze()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(requireContext(), "Maze saved", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Maze failed to save. Check connection.", Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }
}