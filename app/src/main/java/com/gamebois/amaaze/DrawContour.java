package com.gamebois.amaaze;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.graphics.Color.GREEN;

public class DrawContour extends Drawable {
    private ArrayList<PointF> polyPoints;
    private int color;

    public DrawContour(ArrayList<PointF> polyPoints) {
        this.polyPoints = polyPoints;
        this.color = GREEN;
    }

    public DrawContour(ArrayList<PointF> polyPoints, int color) {
        this.polyPoints = polyPoints;
        this.color = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Paint polyPaint = new Paint();
        polyPaint.setColor(color);
        polyPaint.setStyle(Paint.Style.FILL);

        Path wallPath = new Path();
        wallPath.moveTo(polyPoints.get(0).x, polyPoints.get(0).y);
        for (int i = 1; i < polyPoints.size(); i++) {
            PointF p = polyPoints.get(i);
            wallPath.lineTo(p.x, p.y);
        }
        wallPath.close();
        canvas.drawPath(wallPath, polyPaint);
        
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
