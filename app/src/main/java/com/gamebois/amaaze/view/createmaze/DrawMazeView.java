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
    private boolean mRunning;
    public Bitmap mContourBitmap;
    private int mViewWidth;
    private int mViewHeight;
    private PointMarker focusedPoint;
    private SurfaceHolder mSurfaceHolder;

    //Graphics
    private Paint paint;
    private float radius;

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
        this.mSurfaceHolder = getHolder();
        mDrawColour = ResourcesCompat.getColor(getResources(), R.color.colorSecondary, null);
        paint = new Paint();
        paint.setColor(mDrawColour);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);
    }

    //This method is called when a view changes size (such as when it is created), so we can
    //initialise the bitmap here
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewHeight = h;
        this.mViewWidth = w;
        radius = (float) (w / 40);
        focusStartPoint();
        mContourBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        setDrawingCacheEnabled(true);
    }

    public void setPointsRadius(float radiusMultiplier) {
        setPointsRadius(startPoint, radiusMultiplier);
        setPointsRadius(endPoint, radiusMultiplier);
        invalidate();
    }

    private void setPointsRadius(PointMarker pm, float radiusMultiplier) {
        if (pm != null) {
            pm.setRadius(radius * (radiusMultiplier / 2));
        }
    }


//    private ObjectAnimator returnAnimator(PointMarker p) {
//        if (p.getRepeatAnimator() == null) {
//            p.setRepeatAnimator(generateAnimator(p));
//        }
//        return p.getRepeatAnimator();
//    }

//    private void setRadius(float radius) {
//        focusedPoint.setRadius(radius);
//        focusedPoint.getPaint()
//                .setColor(focusedPoint.getColor() + (int) radius / focusedPoint.COLOR_ADJUSTER);
//        invalidate();
//    }

//    private ObjectAnimator generateAnimator(PointMarker p) {
//        ObjectAnimator repeatAnimator = ObjectAnimator.ofFloat(this, "radius", p.getStartRadius(), (float) (p.getStartRadius() * 1.5));
//        repeatAnimator.setInterpolator(new LinearOutSlowInInterpolator());
//        repeatAnimator.setDuration(ANIMATION_DURATION);
//        repeatAnimator.setRepeatCount(1);
//        repeatAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        return repeatAnimator;
//    }

    public void focusStartPoint() {
        if (startPoint == null) {
            startPoint = new PointMarker(mViewHeight, mViewWidth, radius, GREEN);
        }
        this.focusedPoint = startPoint;
        invalidate();
    }

    public void focusEndPoint() {
        if (endPoint == null) {
            endPoint = new PointMarker(mViewHeight, mViewWidth, radius, RED);
        }
        this.focusedPoint = endPoint;
        invalidate();
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
        invalidate();
    }

    private void initialisePaths(Canvas canvas, Bitmap mContourBitmap) {
        canvas.drawBitmap(mContourBitmap, 0, 0, null);
        canvas.drawColor(WHITE);
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
//        ObjectAnimator animator = returnAnimator(focusedPoint);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if (animator.isRunning()) {
//                    animator.cancel();
//                }
                updatePosition(focusedPoint, x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                updatePosition(focusedPoint, x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                animator.start();
                break;
            default:
                //Do nothing
        }
        return true;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                float startX = startPoint.getmX();
                float startY = startPoint.getmY();
                float radius = startPoint.getRadius();
                Paint startPaint = startPoint.getPaint();
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    canvas.save();
                    initialisePaths(canvas, mContourBitmap);
                    canvas.drawCircle(startX, startY, radius, startPaint);
                    if (endPoint != null) {
                        float endX = endPoint.getmX();
                        float endY = endPoint.getmY();
                        Paint endPaint = endPoint.getPaint();
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
