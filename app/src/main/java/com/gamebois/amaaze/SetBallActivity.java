package com.gamebois.amaaze;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SetBallActivity extends AppCompatActivity {

    private String LOG_TAG = SetBallActivity.class.getSimpleName();
    ArrayList<ArrayList<PointF>> rigidsurfaces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRigidSurfaces();
        DrawMazeView drawMazeView;
        drawMazeView = new DrawMazeView(this);
        drawMazeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(drawMazeView);
    }

    private void setRigidSurfaces() {
        Bundle bundle = getIntent().getExtras();
        int size = bundle.getInt("size");
        for (int i = 0; i < size; i++) {
            rigidsurfaces.add(bundle.<PointF>getParcelableArrayList("item" + i));
        }
        String s = "";
        for (PointF p : rigidsurfaces.get(0)) {
            s += p.toString();
        }
        Log.d(LOG_TAG, "Number of Contours (from bundle): " + rigidsurfaces.size() + "n" + rigidsurfaces.get(0).size() + s);

    }
}
