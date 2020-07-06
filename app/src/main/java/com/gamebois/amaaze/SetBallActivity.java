package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SetBallActivity extends AppCompatActivity {
    private String LOG_TAG = SetBallActivity.class.getSimpleName();

    ArrayList<ArrayList<PointF>> rigidsurfaces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ball);

        Bundle bundle = getIntent().getExtras();
        int size = bundle.getInt("size");
        for(int i = 0; i < size; i++){ rigidsurfaces.add(bundle.<PointF>getParcelableArrayList("item" + i));
        }

        Log.d(LOG_TAG, "Number of Contours (from bundle): "+ Integer.toString(rigidsurfaces.size()));

        Toast.makeText(this, "maze", Toast.LENGTH_LONG).show();
    }
}
