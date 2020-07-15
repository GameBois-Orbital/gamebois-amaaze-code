package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.gamebois.amaaze.GameActivity;

import java.util.ArrayList;

public class GraphicSurface extends SurfaceView implements SurfaceHolder.Callback {
    private String LOG_TAG = GameActivity.class.getSimpleName();

    private GraphicThread graphicThread;
    private boolean toggle = false;
    private float azimuth = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;

    private ArrayList<ArrayList<PointF>> mazeArrayList;
    private ArrayList<PointF> ballArrayList;

    public GraphicSurface(Context context) {
        super(context);
        graphicThread = new GraphicThread(this, context); //create game thread;
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setScreenSize(int screen_width, int screen_height) {
        graphicThread.setScreenSize(screen_width, screen_height);
    }

    public void startGraphics() {
        graphicThread.setRunning(true);
        graphicThread.start(); //start game thread
        toggle = true;
        Log.d(LOG_TAG, "GraphicSurface: graphics Started");
    }

    public void stopGraphics()
    {
        if(toggle)
        {
            Log.d(LOG_TAG,"GraphicSurface: about to stop graphics");
            boolean retry = true;
            graphicThread.setRunning(false);
            toggle = false;
            while(retry)
            {
                try
                {
                    graphicThread.join();
                    retry = false;
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        graphicThread.destroyAll(); //destroy maze balls and surfaces
        Log.d(LOG_TAG,"GraphicSurface: graphics stopped");
    }

    public ArrayList<ArrayList<PointF>> getMazeArrayList() {
        return mazeArrayList;
    }

    public ArrayList<PointF> getBallArrayList() {
        return ballArrayList;
    }

    public void setMazeArrayList(ArrayList<ArrayList<PointF>> mazeArrayList) {
        this.mazeArrayList = mazeArrayList;
    }

    public void setBallArrayList(ArrayList<PointF> ballArrayList) {
        this.ballArrayList = ballArrayList;
    }

    /**
     * set azimuth
     */
    public void setAzimuth(float azimuth)
    {
        this.azimuth = azimuth;
    }

    /**
     * set pitch
     */
    public void setPitch(float pitch )
    {
        this.pitch = pitch;
    }

    /**
     *set roll
     */
    public void setRoll(float roll)
    {
        this.roll = roll;
    }

    /**
     * return azimuth to game thread
     */
    public float getAzimuth()
    {
        return azimuth;
    }

    /**
     * return pitch to game thread
     */
    public float getPitch()
    {
        return pitch;
    }

    /**
     * return roll to game thread
     */
    public float getRoll()
    {
        return roll;
    }


}
