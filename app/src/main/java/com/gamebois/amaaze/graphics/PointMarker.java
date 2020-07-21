package com.gamebois.amaaze.graphics;

import androidx.annotation.NonNull;

public class PointMarker {

    private float mX;
    private float mY;
    private float mRadius;

    public PointMarker(int viewWidth, int viewHeight) {
        this.mX = viewWidth / 2;
        this.mY = viewHeight / 2;
        float minParam = viewHeight >= viewWidth ? mY : mX;
        mRadius = minParam / 50;
    }

    public float getmX() {
        return mX;
    }

    public float getmY() {
        return mY;
    }

    public float getmRadius() {
        return mRadius;
    }

    public void update(float newX, float newY) {
        this.mX = newX;
        this.mY = newY;
    }

    @NonNull
    @Override
    public String toString() {
        return "X: " + mX + ", Y: " + mY;
    }
}
