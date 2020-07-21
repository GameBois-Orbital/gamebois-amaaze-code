package com.gamebois.amaaze.view.createmaze;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.MutableLiveData;

import com.gamebois.amaaze.R;
import com.gamebois.amaaze.graphics.PointMarker;

import java.util.List;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class DrawMazeView extends SurfaceView implements Runnable {

    public static final String TAG = DrawMazeView.class.getSimpleName();
    public PointMarker startPoint;
    public PointMarker endPoint;
    //Thread variables
    private Thread mGameThread = null;
    //Additional path variable?
    private boolean mRunning;
    private MutableLiveData<Boolean> startIsSet = new MutableLiveData<>();
    private Context mContext;
    private PointMarker focusedPoint;

    private Paint paint;
    private Paint startPaint;
    private Paint endPaint;
    private Bitmap mExtraContourBitmap;
    private int mViewWidth;
    private int mViewHeight;
    private SurfaceHolder mSurfaceHolder;

    private Path path;

    //Drawing paths
    private int mDrawColour;
    private List<Path> paths;

    DrawMazeView(Context context) {
        this(context, null);
    }

    public DrawMazeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mSurfaceHolder = getHolder();
        mDrawColour = ResourcesCompat.getColor(getResources(), R.color.colorAccentDark, null);
        paint = new Paint();
        paint.setColor(mDrawColour);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);
        startPaint = new Paint();
        startPaint.setColor(GREEN);
        endPaint = new Paint();
        endPaint.setColor(RED);
    }

    //This method is called when a view changes size (such as when it is created), so can
    //initialise the bitmap here
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewHeight = h;
        this.mViewWidth = w;
        this.startIsSet.setValue(false);
        startPoint = new PointMarker(mViewWidth, mViewHeight);
        focusStartPoint();
        mExtraContourBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

    }

    public void focusStartPoint() {
        this.focusedPoint = startPoint;
        invalidate();
    }

    public void focusEndPoint() {
        if (endPoint == null) {
            endPoint = new PointMarker(mViewWidth, mViewHeight);
        }
        this.focusedPoint = endPoint;
        invalidate();
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
        invalidate();
    }

    private void initialisePaths(Canvas canvas) {
        canvas.drawBitmap(mExtraContourBitmap, 0, 0, null);
        if (paths != null) {
            for (Path path : paths) {
                canvas.drawPath(path, paint);
            }
        }
    }

    private void updatePosition(PointMarker pm, float newX, float newY) {
        pm.update(newX, newY);
    }

    public void pause() {
        mRunning = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activateButton();
                updatePosition(focusedPoint, x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                updatePosition(focusedPoint, x, y);
                invalidate();
                break;
            default:
                //Do nothing
        }
        return true;
    }

    private void activateButton() {
        this.startIsSet.postValue(true);
    }

    @Override
    public void run() {
        Canvas canvas;
        while (mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                float startX = startPoint.getmX();
                float startY = startPoint.getmY();
                float radius = startPoint.getmRadius();
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    canvas.save();
                    canvas.drawColor(WHITE);
                    initialisePaths(canvas);
                    canvas.drawCircle(startX, startY, radius, startPaint);
                    if (endPoint != null) {
                        float endX = endPoint.getmX();
                        float endY = endPoint.getmY();
                        canvas.drawCircle(endX, endY, radius, endPaint);
                    }
                    canvas.restore();
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }

}
