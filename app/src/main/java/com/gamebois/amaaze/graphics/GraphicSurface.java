package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.lifecycle.MutableLiveData;

import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.view.CustomChronometer;
import com.gamebois.amaaze.view.GameActivity;

import java.util.ArrayList;
import java.util.List;

public class GraphicSurface extends SurfaceView implements SurfaceHolder.Callback {

    private String LOG_TAG = GameActivity.class.getSimpleName();

    MutableLiveData<Boolean> gameOver;

    public SurfaceHolder holder;
    private GraphicThread graphicThread;
    private boolean toggle = false;
    private float azimuth = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;

    private List<ContourList> mazeArrayList;
    private ArrayList<PointF> wormholesArrayList;
    private PointF startPoint, endPoint;

    float screen_width, screen_height, endPointRadius;

    public GraphicSurface(Context context) {
        super(context);
        graphicThread = new GraphicThread(this, context); //create game thread;
        holder = getHolder();
        holder.addCallback(this);
        gameOver = new MutableLiveData<>();
        gameOver.setValue(false);
    }

    public void setCreatorHeight(float height) {
        graphicThread.setCreatorHeight(height);
    }

    public void setCreatorWidth(float width) {
        graphicThread.setCreatorWidth(width);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopGraphics();
        Log.d(LOG_TAG, "GS destroy surface");
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screen_width = getWidth();
        screen_height = getHeight();
        Log.d(LOG_TAG, "OnSizeChanged " + screen_width + " x " + screen_height);
    }


    public void startGraphics() {
        graphicThread.setScreenSize(screen_width, screen_height);  // passes ScreenSize i.e. View size information to graphicThread
        graphicThread.setGameWorldAndGraphics();
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
                    e.printStackTrace();
                }
            }
        }
        graphicThread.destroyAll(); //destroy maze balls and surfaces
        Log.d(LOG_TAG,"GraphicSurface: graphics stopped");
    }

    public List<ContourList> getMazeArrayList() {
        return mazeArrayList;
    }

    public ArrayList<PointF> getWormholesArrayList() {
        return wormholesArrayList;
    }

    public PointF getStartPoint() {
        return startPoint;
    }

    public PointF getEndPoint() {
        return endPoint;
    }

    public float getEndPointRadius() {
        return endPointRadius;
    }

    public void setMazeArrayList(List<ContourList> mazeArrayList) {
        this.mazeArrayList = mazeArrayList;
    }

    public void setWormholesArrayList(ArrayList<PointF> wormholesArrayList) {
        this.wormholesArrayList = wormholesArrayList;
    }

    public void setStartPoint(PointF pointF) {
        this.startPoint = pointF;
    }

    public void setEndPoint(PointF pointF) {
        this.endPoint = pointF;
    }

    public void setEndPointRadius(float radius) {
        this.endPointRadius = radius;
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

    public MutableLiveData<Boolean> getGameOver() {
        return gameOver;
    }


    public void setCustChrono(CustomChronometer chronometer) {
        graphicThread.setCustChrono(chronometer);
    }
}
