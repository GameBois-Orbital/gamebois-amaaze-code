package com.gamebois.amaaze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.util.List;

public class DrawMazeView extends View {
    private Paint paint;
    private Path path;
    private int mDrawColour;
    private Canvas mExtraCanvas;
    private Bitmap mExtraContourBitmap;
    private List<Path> paths;
    public static final String DRAW_MAZE_LOGTAG = DrawMazeView.class.getSimpleName();

    DrawMazeView(Context context) {
        this(context, null);
    }

    public DrawMazeView(Context context, AttributeSet attributeSet) {
        super(context);
        mDrawColour = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null);
        paint = new Paint();
        paint.setColor(mDrawColour);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);
    }

    //This method is called when a view changes size (such as when it is created), so can
    //initialise the bitmap here
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mExtraContourBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mExtraCanvas = new Canvas(mExtraContourBitmap);
        mExtraCanvas.drawColor(Color.WHITE);
        Log.d(DRAW_MAZE_LOGTAG, paths.toString());

    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
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
