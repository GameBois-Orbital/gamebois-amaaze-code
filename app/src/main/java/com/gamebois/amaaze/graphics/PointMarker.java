package com.gamebois.amaaze.graphics;

import android.animation.ObjectAnimator;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public class PointMarker {

    private float mX;
    private float mY;
    //Animation
    public static final int COLOR_ADJUSTER = 3;
    public ObjectAnimator repeatAnimator;
    private Paint paint;
    private int color;
    private float startRadius;
    private float radius;

    public PointMarker(int h, int w, float radius, int color) {
//        this.mX = (float) (h / 2.0);
//        this.mY = (float) (w / 2.0);
        this.paint = new Paint();
        paint.setColor(color);
        this.radius = radius;
        startRadius = radius;
    }

    public float getmX() {
        return mX;
    }

    public float getmY() {
        return mY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
//        this.paint.setColor(color + (int) radius / 5);
    }

    public float getStartRadius() {
        return startRadius;
    }

    public void setStartRadius(float startRadius) {
        this.startRadius = startRadius;
    }

    public void update(float newX, float newY) {
        this.mX = newX;
        this.mY = newY;
    }

    public Paint getPaint() {
        return paint;
    }

    @NonNull
    @Override
    public String toString() {
        return "X: " + mX + ", Y: " + mY;
    }

    public ObjectAnimator getRepeatAnimator() {
        return repeatAnimator;
    }

    public void setRepeatAnimator(ObjectAnimator repeatAnimator) {
        this.repeatAnimator = repeatAnimator;
    }

    public int getColor() {
        return color;
    }
}
