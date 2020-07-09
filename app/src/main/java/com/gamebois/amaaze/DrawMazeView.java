package com.gamebois.amaaze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class DrawMazeView extends View {
    private Paint paint;
    private Path path;
    private int mDrawColour;
    private Canvas mExtraCanvas;
    private Bitmap mExtraContourBitmap;
    //Iffy methods, do something
    private ArrayList<ArrayList<PointF>> rigidSurfaces = new ArrayList<>();
    private ArrayList<Path> paths = new ArrayList<>(rigidSurfaces.size());

    DrawMazeView(Context context) {
        this(context, null);
    }

    public DrawMazeView(Context context, AttributeSet attributeSet) {
        super(context);
        mDrawColour = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null);
        path = new Path();
        paint = new Paint();
        paint.setColor(mDrawColour);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    //This method is called when a view changes size (such as when it is created), so can
    //initialise the bitmap here
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mExtraContourBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mExtraCanvas = new Canvas(mExtraContourBitmap);
        mExtraCanvas.drawColor(Color.RED);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < rigidSurfaces.size(); i++) {
                    ArrayList<PointF> polyPoints = rigidSurfaces.get(i);
                    Path wallPath = new Path();
                    wallPath.moveTo(polyPoints.get(0).x, polyPoints.get(0).y);
                    for (int j = 1; j < polyPoints.size(); j++) {
                        PointF p = polyPoints.get(j);
                        wallPath.lineTo(p.x, p.y);
                    }
                    wallPath.close();
                    paths.add(wallPath);
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mExtraContourBitmap, 0, 0, null);
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
    }
}
