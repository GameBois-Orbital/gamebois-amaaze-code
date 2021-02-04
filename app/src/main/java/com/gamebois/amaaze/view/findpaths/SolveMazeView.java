package com.gamebois.amaaze.view.findpaths;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.model.pathfinding.Node.Status;

import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;

public class SolveMazeView extends SurfaceView implements Runnable {
    public static final String TAG = SolveMazeView.class.getSimpleName();
    public Bitmap mContourBitmap;
    //Thread variables
    private Thread mGameThread = null;
    private boolean mRunning;
    private int mViewWidth;
    private int mViewHeight;
    private SurfaceHolder mSurfaceHolder;

    //Graphics
    private Paint paint;
    private Paint gridPaint;

    //Drawing paths
    private int mDrawColour;
    private List<Path> paths;
    private Path solutionPath;

    public SolveMazeView(Context context) {
        this(context, null);
    }

    public SolveMazeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        this.mSurfaceHolder = getHolder();
        Log.d(TAG, "Init called");
        mDrawColour = ResourcesCompat.getColor(getResources(), R.color.colorSecondary, null);
        paint = new Paint();
        paint.setColor(BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        gridPaint = new Paint();
        gridPaint.setColor(GREEN);
        gridPaint.setColor(mDrawColour);
        gridPaint.setAntiAlias(true);
        gridPaint.setDither(true);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(10);

    }

    //This method is called when a view changes size (such as when it is created), so we can
    //initialise the bitmap here
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged called");
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewHeight = h;
        this.mViewWidth = w;
        mContourBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public void setPaths(List<Path> paths) {
        Log.d(TAG, "setPaths called");
        Log.d(TAG, paths.toString());
        this.paths = paths;
        invalidate();
    }

    public void setSolution(Path solutionPath) {
        this.solutionPath = solutionPath;
        invalidate();
    }


    private void initialisePathsAndGrid(Canvas canvas, Bitmap mContourBitmap) {
        canvas.drawBitmap(mContourBitmap, 0, 0, null);
        canvas.drawColor(WHITE);
        if (paths != null) {
            for (Path path : paths) {
                canvas.drawPath(path, paint);
            }
        }
        if (solutionPath != null) {
            canvas.drawPath(solutionPath, gridPaint);
        }
    }

    private Paint getPaint(Status status) {
        Log.d(TAG, "getPaint called");
        switch (status) {
            case END:
                gridPaint.setColor(RED);
                return gridPaint;
            case START:
                gridPaint.setColor(GREEN);
                return gridPaint;
            case VISITED:
                gridPaint.setColor(BLUE);
                return gridPaint;
            case UNVISITED:
                gridPaint.setColor(TRANSPARENT);
                return gridPaint;
            case FRONTIER:
                gridPaint.setColor(GRAY);
                return gridPaint;
            default:
                return gridPaint;
        }
    }

    public void pause() {
        mRunning = false;
        Log.d(TAG, "pause called");
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void resume() {
        Log.d(TAG, "resume called");
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @Override
    public void run() {
        Canvas canvas;
        while (mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                Log.d(TAG, "run called");
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    canvas.save();
                    initialisePathsAndGrid(canvas, mContourBitmap);
                    canvas.restore();
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }
}
