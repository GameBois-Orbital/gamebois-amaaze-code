package com.gamebois.amaaze;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
        for (int i = 0; i < size; i++) {
            rigidsurfaces.add(bundle.<PointF>getParcelableArrayList("item" + i));
        }
        String s = "";
        for (PointF p : rigidsurfaces.get(0)){
            s += p.toString();
        }
        Log.d(LOG_TAG, "Number of Contours (from bundle): "+ Integer.toString(rigidsurfaces.size()) + "n"+ Integer.toString(rigidsurfaces.get(0).size()) + s);

        /*for (ArrayList<PointF> surface : rigidsurfaces) {
            DrawContour mazeDrawing = new DrawContour(surface);
            ImageView image = findViewById(R.id.my_image_view);
            image.setImageDrawable(mazeDrawing);
        } */
    }
}
